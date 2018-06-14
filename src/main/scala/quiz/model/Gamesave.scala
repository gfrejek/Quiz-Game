package quiz.model

import java.util.{Calendar, Date}
import java.text.SimpleDateFormat

@SerialVersionUID(158L)
class Gamesave private (var player: Player, var score: Int, var currentQuestion: Int, var numberOfQuestions: Int, var date : Date, var questionsSource: QuestionsSource) extends Serializable {

  def >(other: Gamesave): Boolean = {
    this.date.after(other.date)
  }

  def <(other: Gamesave): Boolean = {
    this.date.before(other.date)
  }

  def toGame(): Game = {
    var loadedGame = Game(player, questionsSource)
    loadedGame.score() = this.score
    loadedGame.currentQuestion() = this.currentQuestion
    loadedGame
  }

  def display(): String = {
    var result = StringBuilder.newBuilder
    result.append(player.name)
    result.append("\t")
    result.append(score)
    result.append("\t")
    result.append(currentQuestion + "/" + numberOfQuestions)
    result.append("\t")
    val sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val strDate = sdfDate.format(date)
    result.append(strDate.toString)
    result.toString
  }

}

@transient
object Gamesave {

  def apply(game: Game) = {
    new Gamesave(game.player, game.score(), game.currentQuestion(), game.numberOfQuestions, Calendar.getInstance().getTime, game.data)
  }

}