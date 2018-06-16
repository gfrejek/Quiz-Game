package quiz.generators

import quiz.model._


object QuestionGenerator {

  def generate(source: QuestionsSource, count: Int): Option[List[Question]] = {
    source match {
      case NumbersAPI() =>
        NumbersAPIGenerator.generateBatch(count)
      case OpenTDB() =>
        OpenTDBGenerator.generateBatch(count)
      case JService() =>
        JServiceGenerator.generateBatch(count)
    }
  }
}