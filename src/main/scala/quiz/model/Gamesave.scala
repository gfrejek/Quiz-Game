package quiz.model

import java.util.{Calendar, Date}
import java.text.SimpleDateFormat

@SerialVersionUID(158L)
class Gamesave private (var player: Player, var score: Int, var currentQuestion: Int, 
  var numberOfQuestions: Int, var date : Date, var questionsSource: QuestionsSource,
  var fiftyFiftyUsed: Boolean, var phoneAFriendUsed: Boolean, var askTheAudienceUsed: Boolean) extends Serializable {
  
  def >(other: Gamesave): Boolean = {
    this.date.after(other.date)
  }

  def <(other: Gamesave): Boolean = {
    this.date.before(other.date)
  }

  def toGame: Game = {
    var loadedGame = Game(player, questionsSource)
    loadedGame.score() = this.score
    loadedGame.currentQuestion() = this.currentQuestion
    loadedGame.fiftyFiftyUsed = this.fiftyFiftyUsed
    loadedGame.phoneAFriendUsed = this.phoneAFriendUsed
    loadedGame.askTheAudienceUsed = this.askTheAudienceUsed
    loadedGame
  }

  def display(): String = {
    val sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val strDate = sdfDate.format(date)
    var result = StringBuilder.newBuilder
    val questionNr = "(" + (currentQuestion + 1) + "/" + numberOfQuestions + ")"

    val initialLength = score.toString.length + questionNr.length + 4
    val spacesLeft = 24 - (initialLength + player.name.length)
    if(spacesLeft < 0){
      result.append(player.name.substring(0, player.name.length + spacesLeft - 1) + "~")
      result.append("  ")
    } else if(spacesLeft == 0){
      result.append(player.name)
      result.append("  ")
    } else {
      val spaces = spacesLeft + 2
      result.append(player.name)
      result.append(" " * spaces)
    }

    result.append(score)
    result.append("  ")
    result.append(questionNr)
    result.toString
  }

  def canEqual(a: Any): Boolean = a.isInstanceOf[Gamesave]

  override def equals(that: Any): Boolean =
    that match {
      case that: Gamesave => that.canEqual(this) && this.hashCode == that.hashCode
      case _ => false
    }
  override def hashCode: Int = {
    7 * score + 11 * date.hashCode + 13 * currentQuestion + 17 * numberOfQuestions
  }

}

@transient
object Gamesave {
  def apply(game: Game): Gamesave = {
    new Gamesave(game.player, game.score(), game.currentQuestion() - 1, game.numberOfQuestions, 
      Calendar.getInstance().getTime, game.data, game.fiftyFiftyUsed, game.phoneAFriendUsed, game.askTheAudienceUsed)
  }

}