package quiz.model

import quiz.controller.Controller

@SerialVersionUID(121L)
sealed abstract class QuestionsSource extends Serializable
case class NumbersAPI() extends QuestionsSource
case class OpenTDB() extends QuestionsSource
case class JService() extends QuestionsSource

object QuestionsSource {
  val numbersAPI = NumbersAPI()
  val jService = JService()
  val openTDB = OpenTDB()
}

case class Question(var question: String, var answer: List[String], var correctAnswer: String)

class Model(controller: Controller) {

}