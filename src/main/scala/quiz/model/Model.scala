package quiz.model

import quiz.controller.Controller

@SerialVersionUID(121L)
sealed abstract class QuestionsSource extends Serializable
case class NumbersAPI() extends QuestionsSource
case class OpenTDB() extends QuestionsSource
case class TheSportsDB() extends QuestionsSource

object QuestionsSource {
  val numbersAPI = new NumbersAPI()
  val openTDB = new OpenTDB()
  val theSportsDB = new TheSportsDB()
}

case class Question(var question: String, answer: List[String], correctAnswer: String)

class Model(controller: Controller) {

}