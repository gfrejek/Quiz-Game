package quiz.model

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