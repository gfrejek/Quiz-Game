package quiz.generators

import quiz.model.Question
import quiz.fetchers.OpenTDBFetcher
import spray.json._
import DefaultJsonProtocol._
import org.apache.commons.text.StringEscapeUtils

case class QuestionWrapper(questions: List[Question])

object OpenTDBGenerator extends Generator {

  implicit object QuestionWrapperJsonFormat extends RootJsonFormat[QuestionWrapper] {
    // We will not use write anyway
    def write(q: QuestionWrapper): JsValue = new JsObject (Map[String, JsValue]())
    def read(value: JsValue): QuestionWrapper = {
      value.asJsObject.getFields("results") match {
        case Seq(listOfQuestions) =>
          implicit object QuestionJsonFormat extends RootJsonFormat[Question] {
            // Same here
            def write(q: Question): JsValue = new JsObject (Map[String, JsValue]())
            def read(value: JsValue): Question = {
              value.asJsObject.getFields("question", "correct_answer", "incorrect_answers") match {
                case Seq(JsString(question), JsString(correct), incorrect) =>
                  Question(question, incorrect.convertTo[List[String]], correct)
                case _ => throw DeserializationException("Question Expected")
              }
            }
          }
          QuestionWrapper(listOfQuestions.convertTo[List[Question]])
        case _ => throw DeserializationException("Question Expected")
      }
    }
  }
  
  override def generateBatch(count: Int): Option[List[Question]] = {
    var result: Option[List[Question]] = None
    try {
      val jsonQuestionsStr = OpenTDBFetcher.fetchBatch(count)
      result = Some(jsonQuestionsStr.parseJson.convertTo[QuestionWrapper].questions)
    } catch {
      case e: Throwable =>
        println("Error while generating question batch from OpeTDB")
        result = None
    }
    result match {
      case Some(res) =>
        for (r <- res) {
          r.answer = r.answer.map(StringEscapeUtils.unescapeHtml4)
          r.correctAnswer = StringEscapeUtils.unescapeHtml4(r.correctAnswer)
          r.question = StringEscapeUtils.unescapeHtml4(r.question)
        }
        Some(res)
      case None => None
    }
  }

}