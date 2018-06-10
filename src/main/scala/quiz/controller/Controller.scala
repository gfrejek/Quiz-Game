package quiz.controller

import java.util.Calendar

import com.typesafe.config.Config
import scalafx.scene.Scene
import scalafx.stage.Stage
import quiz.model._
import quiz.generators.QuestionGenerator
import scalafx.beans.property.StringProperty

class Controller(val config: Config) {

  var currentStage: Stage = _
  var currentGame: Game = _
  var currentQuestionList: List[Question] = _
  var question: Question = _
  val highscoreManager = new HighscoreManager()
  val gamesaveManager = new GamesaveManager()
  var iterator: Iterator[Question] = _

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

    currentQuestionList = QuestionGenerator.generate(data, 12) match {
      case Some(validList) => validList
      case None => null   // TODO Exception handling
    }
    iterator = currentQuestionList.iterator
  }

  def continueGame(game: Game) = {
    currentGame = game
    val howManyLeft = game.numberOfQuestions - game.currentQuestion()
    currentQuestionList = QuestionGenerator.generate(game.data, howManyLeft) match {
      case Some(validList) => validList
      case None => null   // TODO Exception handling
    }
    iterator = currentQuestionList.iterator
  }

  def gameNotFinished(): Boolean = {
    currentGame.currentQuestion() < currentGame.numberOfQuestions
  }

  def askNextQuestion(): Question = {
    question = iterator.next()
    currentGame.currentQuestion() += 1
    question
  }

  def respondToUserChoice(choice: String, elapsedTime: Int): Boolean = {
    if (choice == question.correctAnswer) {
      currentGame.score() += (1000 / elapsedTime)
      true
    } else {
      false
    }
  }

  def concludeGame(): Int = {
    val finalScore = currentGame.score()
    currentGame = null
    currentQuestionList = null
    finalScore
  }

}