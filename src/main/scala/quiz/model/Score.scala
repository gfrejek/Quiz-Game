package quiz.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar

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
    val sdfDate = new SimpleDateFormat("yyyy-MM-dd")
    val strDate = sdfDate.format(date)
    val initialLength = score.toString.length + strDate.toString.length + 6
    val spacesLeft = 32 - (initialLength + playername.length)
    if(spacesLeft < 0){
      result.append(playername.substring(0, playername.length + spacesLeft - 1) + "~")
      result.append("  ")
    } else if(spacesLeft == 0){
      result.append(playername)
      result.append("  ")
    } else {
      val spaces = spacesLeft + 2
      result.append(playername)
      result.append(" " * spaces)
    }
    result.append(score)
    result.append("  ")
    result.append("[" + strDate.toString + "]")
    result.toString
  }

}