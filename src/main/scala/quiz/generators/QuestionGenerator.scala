package quiz.generators

import quiz.model.Question
import quiz.model.QuestionsSource
import quiz.generators.OpenTDBGenerator._


object QuestionGenerator {

  def generate(source: QuestionsSource, count: Int): Option[List[Question]] = {
    source match {
      // case NumbersAPI() => {

      // }
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