package quiz.model

import quiz.controller.Controller


case class Player(name: String)

sealed abstract class QuestionsSource
case class NumbersAPI() extends QuestionsSource
case class OpenTDB() extends QuestionsSource
case class TheSportsDB() extends QuestionsSource

object QuestionsSource {
  val numbersAPI = new NumbersAPI()
  val openTDB = new OpenTDB()
  val theSportsDB = new TheSportsDB()
}

case class Question(val qs: String, val ans: List[String], val correct: String)

case class Game(player: Player, data: QuestionsSource)

class Model(controller: Controller) {

}