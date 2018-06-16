package quiz.controller

import java.time.{Duration, Instant}
import java.util.{Timer, TimerTask}

import com.typesafe.config.Config
import quiz.generators.QuestionGenerator
import quiz.model._
import scalafx.beans.property.StringProperty
import scalafx.scene.Scene
import scalafx.stage.Stage

import scala.util.Random

class Controller(val config: Config) {

  var currentStage: Stage = _
  var currentGame: Game = _
  var currentQuestionList: List[Question] = _
  var question: Question = _
  val highscoreManager = new HighscoreManager()
  val gamesaveManager = new GamesaveManager()
  var iterator: Iterator[Question] = _
  val choiceA: StringProperty = StringProperty("")
  val choiceB: StringProperty = StringProperty("")
  val choiceC: StringProperty = StringProperty("")
  val choiceD: StringProperty = StringProperty("")
  val questionContents: StringProperty = StringProperty("")
  val scoreStr: StringProperty = StringProperty("")
  val progressStr: StringProperty = StringProperty("")
  var startTimestamp: Instant = _
  var clock: Clock = new Clock()
  var clockString: StringProperty = StringProperty("")
  var timer: Timer = _
  var currentTimerTask: TimerTask = _

  var highscoreString = StringProperty(highscoreManager.getHighscoreString)
  var gamesaveList = StringProperty(gamesaveManager.getGamesaveString)

  def passControl(passed: Stage): Unit = {
    currentStage = passed
  }

  def changeScene(scene: Scene): Unit = {
    currentStage.scene = scene
    currentStage.fullScreen = true
  }
  
  def closeStage(): Unit = {
    currentStage.close()
  }

  def startNewGame(player: Player, data: QuestionsSource): Unit = {
    currentGame = Game.newGame(player, data)
    currentGame.currentQuestion() = 0

    currentQuestionList = QuestionGenerator.generate(data, 12) match {
      case Some(validList) => validList
      case None => null   // TODO Exception handling
    }

    iterator = currentQuestionList.iterator

    scoreStr <== currentGame.score.asString
    progressStr <== currentGame.currentQuestion.asString

    timer = new Timer(true)
    askNextQuestion()
  }

  def continueGame(game: Game): Unit = {
    currentGame = game
    val howManyLeft = game.numberOfQuestions - game.currentQuestion()
    currentQuestionList = QuestionGenerator.generate(game.data, howManyLeft) match {
      case Some(validList) => validList
      case None => null   // TODO Exception handling
    }
    iterator = currentQuestionList.iterator

    scoreStr <== currentGame.score.asString
    progressStr <== currentGame.currentQuestion.asString

    timer = new Timer(true)
    askNextQuestion()
  }

  def gameNotFinished(): Boolean = {
    currentGame.currentQuestion() < currentGame.numberOfQuestions
  }

  def askNextQuestion(): Unit = {
    question = iterator.next()
    currentGame.currentQuestion() += 1

    val choiceList = question.correctAnswer :: question.answer
    val shuffled = Random.shuffle(choiceList)

    choiceA() = shuffled.head
    choiceB() = shuffled(1)
    choiceC() = shuffled(2)
    choiceD() = shuffled(3)
    questionContents() = question.question
    startTimestamp = Instant.now()
    clockString() = "00:00"
    clock.init()
    timer.scheduleAtFixedRate(clockTask(), 0, 1000)
  }

  def respondToUserChoice(choice: String, elapsedTime: Int): Boolean = {
    currentTimerTask.cancel()
    timer.purge()
    if (choice == question.correctAnswer) {
      val endTimestamp: Instant = Instant.now()
      val elapsedTime: Duration = Duration.between(startTimestamp, endTimestamp)
      var elapsedTimeInSec = elapsedTime.getSeconds - 4
      if(elapsedTimeInSec < 1) elapsedTimeInSec = 1
      currentGame.score() += (1000 * Math.pow(0.9, elapsedTimeInSec-1)).toInt
    }
    if(gameNotFinished()) {
      askNextQuestion()
      false
    } else {
      concludeGame()
      true
    }
  }

  def concludeGame(): Unit = {
    timer.cancel()
    highscoreManager.addScore(new Score(currentGame.score(), currentGame.player.name))
  }

  def clockTask(): TimerTask = {
    currentTimerTask = new TimerTask(){
      def run(){
        clockString() = clock.toString
        clock.inc()
      }
    }
    currentTimerTask
  }

}

class Clock() {
  var minutes: Long = 0
  var seconds: Long = 0

  def init(): Unit = {
    minutes = 0
    seconds = 0
  }

  def inc(): Unit = {
    seconds = (seconds + 1) % 60
    if(seconds == 0) minutes += 1
  }

  override def toString: String = {
    s"%02d:%02d".format(minutes, seconds)
  }
}