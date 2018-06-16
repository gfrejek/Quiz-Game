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
  var pauseTimestamp: Instant = _
  var timeSpentInPause: Duration = _
  var clock: Clock = new Clock()
  val clockString: StringProperty = StringProperty("")
  var timer: Timer = _
  var currentTimerTask: TimerTask = _
  val random: Random = new Random()

  val highscoreString = StringProperty(highscoreManager.getHighscoreString())
  val gamesaveList = StringProperty(gamesaveManager.getGamesaveString())


  def passControl(passed: Stage) = {
    currentStage = passed
  }


  def changeScene(scene: Scene) = {
    currentStage.scene = scene
    currentStage.fullScreen = true
  }
  

  def closeStage() = {
    currentStage.close()
  }


  def startNewGame(player: Player, data: QuestionsSource) = {
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


  def continueGame(game: Game) = {
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


  def askNextQuestion() = {
    question = iterator.next()
    currentGame.currentQuestion() += 1

    val choiceList = question.correctAnswer :: question.answer
    val shuffled = random.shuffle(choiceList)

    choiceA() = shuffled(0)
    choiceB() = shuffled(1)
    choiceC() = shuffled(2)
    choiceD() = shuffled(3)
    questionContents() = question.question

    startTimestamp = Instant.now()
    clockString() = "00:00"
    clock.init()
    timeSpentInPause = Duration.ZERO
    timer.scheduleAtFixedRate(clockTask(), 0, 1000)
  }


  def respondToUserChoice(choice: String): Boolean = {
    currentTimerTask.cancel()
    timer.purge()
    if (choice == question.correctAnswer) {
      val endTimestamp: Instant = Instant.now()
      val overallElapsedTime: Duration = Duration.between(startTimestamp, endTimestamp)
      val gameElapsedTime: Duration = overallElapsedTime.minus(timeSpentInPause)
      var elapsedTimeInSec = gameElapsedTime.getSeconds - 4
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


  def concludeGame() = {
    timer.cancel()
    highscoreManager.addScore(new Score(currentGame.score(), currentGame.player.name))
  }


  def pauseGame() = {
    currentTimerTask.cancel()
    timer.purge()
    pauseTimestamp = Instant.now()
  }


  def resumeGame() = {
    val pauseStopTimestamp = Instant.now()
    val thisPauseDuration = Duration.between(pauseTimestamp, pauseStopTimestamp)
    timeSpentInPause = timeSpentInPause.plus(thisPauseDuration)

    timer.scheduleAtFixedRate(clockTask(), 0, 1000)
  }


  def useFiftyFifty() = {
    currentGame.fiftyFiftyUsed = true

    List(question.answer(0), question.answer(1))
  }


  def usePhoneAFriend(): String = {
    currentGame.phoneAFriendUsed = true
    val choice = if (random.nextInt(100) < 75) question.correctAnswer else {
      val randomElse = random.nextInt(3)
      question.answer(randomElse)
    }
    
    { "Oh, hi " + currentGame.player.name + ", it's really nice to hear from you. " +
    "What?! You are playing the Quiz right now on the TV?! That's amazing news! " +
    "And you need my help? Well, I'll try my best but I can't make any promises, " +
    "that's for sure. Okay, go ahead, read me the question. Okay... Right... " +
    "Hmm... I think the correct answer is " + choice + ", however I'm not entirely " +
    "sure." }
  }


  def useAskTheAudience() = {
    currentGame.askTheAudienceUsed = true
    var pollRes = List[(String, Int)]()
    val choiceList = question.correctAnswer :: question.answer
    val shuffled = random.shuffle(choiceList)

    def assignVotes(choice: String) = {
      if (choice == question.correctAnswer) {
        random.nextInt(50) + 150
      } else {
        random.nextInt(100)
      }
    }

    for (choice <- shuffled) {
      pollRes = (choice, assignVotes(choice)) :: pollRes
    }

    pollRes
  }


  def clockTask() = {
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