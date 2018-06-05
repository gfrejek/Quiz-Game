package quiz.fetchers

import scalaj.http.{Http, HttpRequest}


object OpenTDBFetcher extends Fetcher {

  override def fetchBatch(count: Int): String = {
    var jsonQuestionStr: String = ""
    
    val request = Http("https://opentdb.com/api.php?amount=" + count.toString + "&difficulty=medium&type=multiple")
    jsonQuestionStr = request.asString.body

    jsonQuestionStr
  }

}