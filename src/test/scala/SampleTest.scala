import org.specs2._
import quiz.utils._

class SampleSpec extends Specification { def is = s2"""

 This is a sample specificatation to check various functions

 The function 'listTo3Tuple' should
   convert a 3 element list to 3-tuple                $e1
   convert a more than 3 element list to a 3-Tuple    $e2
   return a null for a list with less than 3 elements $e3
                                                      """

  def e1 = listTo3Tuple(List("Ala", "ma", "kota")) must_==(("Ala", "ma", "kota"))
  def e2 = {
    val sampleList = List("Ala", "ma", "zielonego", "kota")
    listTo3Tuple(sampleList) must_==(("Ala", "ma", "zielonego"))
  }
  def e3 = {
    val listWith2Elems = List("Ala", "ma")
    listTo3Tuple(listWith2Elems) must_==(null)
  }
}