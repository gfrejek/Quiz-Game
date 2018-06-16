package quiz.fetchers

import scalaj.http.Http

import scala.util.Random

object JServiceFetcher extends Fetcher {
  val categories: List[String] = List("7", "21", "105", "25", "67", "49", "582", "109") // 25?
  val address = "http://jservice.io/api/clues?category="
  override def fetchBatch(count: Int): String = {
    val rand = new Random(System.currentTimeMillis())
    val randomIndex = rand.nextInt(categories.length)
    val randomElement = categories(randomIndex)
    Http(address + randomElement).asString.body
  }

}
