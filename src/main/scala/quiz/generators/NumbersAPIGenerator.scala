package quiz.generators

import quiz.fetchers.NumbersAPIFetcher
import quiz.model.Question

import scala.util.Random

object NumbersAPIGenerator extends Generator {
  override def generateBatch(count: Int): Option[List[Question]] = {
    var result: List[Question] = List()
    try {
      val questions = NumbersAPIFetcher.fetchBatch(count)
      for(line <- questions.split("\n")){
        val question = "What " + line.substring(line.indexOf(' ') + 1, line.length - 1) + "?"
        val correctAnswer = line.substring(0, line.indexOf(' '))
        val otherAnswers = generateIncorrectAnswers(correctAnswer)
        result = Question(question, otherAnswers, correctAnswer) :: result
      }
    } catch {
      case e: Throwable => {
        println(e.toString + "Error while generating question batch from NumbersAPI")
      }
    }
    Option(result)
  }

  private def generateIncorrectAnswers(initialNumberAsString: String) = {
    val initialNumber = initialNumberAsString.toInt
    var listOfNumbers: List[String] = List()
    for(i <- 1 to 3)  {
      val rand = new Random()
      val randModifier = 1.05 + (rand.nextInt(2) * -0.1)
      var randomAnswer = initialNumber * randModifier
      while(rand.nextInt(3) % 2 == 0) randomAnswer *= randModifier
      while(listOfNumbers.contains(randomAnswer.toInt.toString) || randomAnswer.toInt == initialNumber){
        var negModifier = 1
        if(randModifier < 1.0) negModifier = -1
        if((randModifier * randomAnswer).toInt - randomAnswer == 0) randomAnswer += negModifier
        randomAnswer *= randModifier
      }
      listOfNumbers = randomAnswer.toInt.toString :: listOfNumbers
    }
    listOfNumbers
  }

}

