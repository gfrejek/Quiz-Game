package quiz.generators

import quiz.model.Question
import spray.json._
import DefaultJsonProtocol._ 


trait Generator {
  
  def generateBatch(count: Int): Option[List[Question]]

}