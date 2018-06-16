package quiz.model

import scalafx.beans.property.IntegerProperty

case class Game private (val player: Player, val data: QuestionsSource) {

  var score = IntegerProperty(0)
  var currentQuestion = IntegerProperty(0)
  val numberOfQuestions: Int = 12
  var fiftyFiftyUsed: Boolean = false
  var phoneAFriendUsed: Boolean = false
  var askTheAudienceUsed: Boolean = false
  
}

object Game {

  def newGame(player: Player, data: QuestionsSource) = {
    new Game(player, data)
  }

}