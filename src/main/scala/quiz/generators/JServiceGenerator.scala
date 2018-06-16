package quiz.generators

import quiz.fetchers.JServiceFetcher
import quiz.model.Question
import spray.json._

import scala.util.Random


case class Category(id: Int, title: String)
case class Clue(id: Int, answer: String, question: String)
case class ClueArray(items: List[Clue]) extends IndexedSeq[Clue] {
  def apply(index: Int) = items(index)
  def length: Int = items.length
}

object JServiceGenerator extends Generator {

  object CluesProtocol extends DefaultJsonProtocol {
    implicit val clueFormat: RootJsonFormat[Clue] = jsonFormat3(Clue)
    implicit object clueListJsonFormat extends RootJsonFormat[ClueArray] {
      def read(value: JsValue) = ClueArray(value.convertTo[List[Clue]])
      def write(f: ClueArray): JsValue = ???
    }
  }

  import CluesProtocol._

  override def generateBatch(count: Int): Option[List[Question]] = {
    var questionList: List[Question] = List()
    try {
      val input = JServiceFetcher.fetchBatch(count).parseJson
      val clues = input.convertTo[ClueArray]
      val randomOrderClues = Random.shuffle(clues)
      for (i <- 1 to count) {
        val question = clues(i).question
        val correctAnswer = cleanAnswer(clues(i).answer)
        val incorrectAnswers: List[String] = for (j <- (2 to 4).toList) yield {
          cleanAnswer(clues(j * i).answer)
        }
        questionList = Question(question, incorrectAnswers, correctAnswer) :: questionList
      }
    } catch {
      case e: Throwable =>
        println(e.toString + "Error while generating question batch from JService")
    }
    Option(questionList)
  }

  private def cleanAnswer(answer: String): String = {
    val startIndex = answer.indexOf(">")
    val endIndex = answer.lastIndexOf("<")
    if (answer.indexOf("<") == -1) answer else answer.substring(startIndex + 1, endIndex)
  }
}
