package quiz.generators

import quiz.model.{NumbersAPI, Question, QuestionsSource}


object QuestionGenerator {

  def generate(source: QuestionsSource, count: Int): Option[List[Question]] = {
    source match {
      case NumbersAPI() => {
        NumbersAPIGenerator.generateBatch(count)
      }
      case QuestionsSource.openTDB => {
        OpenTDBGenerator.generateBatch(count)
      }
      case _ => {
        null
      }
      // case TheSportsDB() => {

      // }
    }
  }

}