package quiz.model

import java.io.Serializable
import java.util.{Calendar, Date}
import java.text.SimpleDateFormat

@SerialVersionUID(11L)
class Score (val score : Int, val playername: String) extends Serializable {

  val date = Calendar.getInstance().getTime

  def >(score2: Score): Boolean = {
    this.score > score2.score
  }

  def <(score2: Score): Boolean = {
    this.score < score2.score
  }

  def display(): String = {
    var result = StringBuilder.newBuilder
    result.append(playername)
    result.append("\t")
    result.append(score)
    result.append("\t")
    val sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val strDate = sdfDate.format(date)
    result.append(strDate.toString)
    result.toString
  }

}