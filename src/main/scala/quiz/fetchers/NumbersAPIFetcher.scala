package quiz.fetchers

import scalaj.http.Http


object NumbersAPIFetcher extends Fetcher {

  override def fetchBatch(count: Int): String = {
    var result = StringBuilder.newBuilder

    for(i <- 1 to count){
      var question = newQuestion()
      for{
        triesNumber <- 1 to 5
        if !correctQuestion(question)
      } question = newQuestion()

      if(!correctQuestion(question)) throw new ExceptionInInitializerError("Incorrect question")

      result.append(question)
      result.append("\n")
    }

    result.toString()
  }

  private def correctQuestion(question: String): Boolean = {
    try{
      val correctAnswer = question.substring(0, question.indexOf(' '))
      val number = correctAnswer.toInt
      true
    } catch {
      case e: java.lang.NumberFormatException => false
      case e: Throwable => false
    }
  }

  private def newQuestion() = {
    var httpString = Http("http://numbersapi.com/random/trivia").asString.body
    while(httpString.contains("Bad Gateway")) httpString = Http("http://numbersapi.com/random/trivia").asString.body
    httpString
  }

}