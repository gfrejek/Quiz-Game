package quiz

package object utils {

  def listTo3Tuple(list: List[String]): (String, String, String) = {
    list match {
      case List(a, b, c, _*) => (a, b, c)
    }
  }

}
