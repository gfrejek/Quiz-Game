package quiz.controller

import com.typesafe.config.Config
import scalafx.scene.Scene
import scalafx.stage.Stage
import quiz.model.Game
import quiz.model.Player
import quiz.model.QuestionsSource
import quiz.generators.QuestionGenerator
import quiz.model.Question

class Controller(val config: Config) {

  var currentStage: Stage = _
  var currentGame: Game = _
  var currentQuestionList: List[Question] = _
  
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
      case None => null
    }
  }

  def continueGame(game: Game) = {
    currentGame = game
    val howManyLeft = game.numberOfQuestions - game.currentQuestion()
    currentQuestionList = QuestionGenerator.generate(game.data, howManyLeft) match {
      case Some(validList) => validList
      case None => null
    }
  }

  def gameNotFinished(): Boolean = {
    currentGame.currentQuestion() < currentGame.numberOfQuestions
  }

  def askNextQuestion(): Question = {
    val realIndex = currentGame.currentQuestion() - currentGame.doneInLastSession
    val next = currentQuestionList(realIndex)
    currentGame.currentQuestion() += 1
    next
  }

  def respondToUserChoice(choice: String, elapsedTime: Int): Boolean = {
    val realIndex = currentGame.currentQuestion() - currentGame.doneInLastSession
    if (choice == currentQuestionList(realIndex).correct) {
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