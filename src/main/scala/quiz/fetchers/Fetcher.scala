package quiz.fetchers


trait Fetcher {

  def fetchBatch(count: Int): String
  
}