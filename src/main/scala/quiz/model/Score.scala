package quiz.model

import java.io.Serializable
import java.util.{Calendar, Date}

@SerialVersionUID(11L)
class Score (val score : Int, val playername: String) extends Serializable {

  val date = Calendar.getInstance().getTime

  def >(score2: Score): Boolean = {
    this.score > score2.score
  }

  def <(score2: Score): Boolean = {
    this.score < score2.score
  }
}