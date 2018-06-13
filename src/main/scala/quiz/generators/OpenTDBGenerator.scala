package quiz.generators

import quiz.model.Question
import quiz.fetchers.OpenTDBFetcher
import spray.json._
import DefaultJsonProtocol._ 

case class QuestionWrapper(val questions: List[Question])

object OpenTDBGenerator extends Generator {

  implicit object QuestionWrapperJsonFormat extends RootJsonFormat[QuestionWrapper] {
    // We will not use write anyway
    def write(q: QuestionWrapper): JsValue = new JsObject (Map[String, JsValue]())
    def read(value: JsValue) = {
      value.asJsObject.getFields("results") match {
        case Seq(listOfQuestions) => {
          implicit object QuestionJsonFormat extends RootJsonFormat[Question] {
            // Same here
            def write(q: Question): JsValue = new JsObject (Map[String, JsValue]())   //TODO Why two JSON objects?
            def read(value: JsValue) = {
              value.asJsObject.getFields("question", "correct_answer", "incorrect_answers") match {
                case Seq(JsString(question), JsString(correct), incorrect) =>
                  Question(question, incorrect.convertTo[List[String]], correct)
                case _ => throw new DeserializationException("Question Expected")
              }
            }
          }
          QuestionWrapper(listOfQuestions.convertTo[List[Question]])
        }
        case _ => throw new DeserializationException("Question Expected")
      }
    }
  }
  
  override def generateBatch(count: Int): Option[List[Question]] = {
    var result: Option[List[Question]] = None
    try {
      val jsonQuestionsStr = OpenTDBFetcher.fetchBatch(count)
      result = Some(jsonQuestionsStr.parseJson.convertTo[QuestionWrapper].questions)
    } catch {
      case e: Throwable => {
        println("Error while generating question batch from OpeTDB")
        result = None
      }
    }
    result match {
      case Some(res) => {
        for (r <- res) {
          r.question = r.question.replaceAll("&quot;", "\"")
        }
        Some(res)
      }
      case None => None
    }
  }

}