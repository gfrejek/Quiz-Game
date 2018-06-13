package quiz.controller

import java.util.Calendar

import com.typesafe.config.Config
import scalafx.scene.Scene
import scalafx.stage.Stage
import quiz.model._
import quiz.generators.QuestionGenerator
import scalafx.beans.property.StringProperty
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

  var highscoreString = StringProperty(highscoreManager.getHighscoreString())
  //TODO not the right place to put it
  var gamesaveList = StringProperty(gamesaveManager.getGamesaveString())

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

    askNextQuestion()
  }

  def gameNotFinished(): Boolean = {
    currentGame.currentQuestion() < currentGame.numberOfQuestions
  }

  def askNextQuestion() = {
    question = iterator.next()
    currentGame.currentQuestion() += 1

    val choiceList = question.correctAnswer :: question.answer
    val shuffled = Random.shuffle(choiceList)

    choiceA() = shuffled(0)
    choiceB() = shuffled(1)
    choiceC() = shuffled(2)
    choiceD() = shuffled(3)
    questionContents() = question.question
  }

  def respondToUserChoice(choice: String, elapsedTime: Int): Boolean = {
    if (choice == question.correctAnswer) {
      currentGame.score() += (1000 / elapsedTime)
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
    highscoreManager.addScore(new Score(currentGame.score(), currentGame.player.name))
  }

}