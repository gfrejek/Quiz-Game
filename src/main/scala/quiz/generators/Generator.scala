package quiz.generators

import quiz.model.Question


trait Generator {
  
  def generateBatch(count: Int): Option[List[Question]]

}