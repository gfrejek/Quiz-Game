package quiz.model

import java.util.{Calendar, Date}

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
}

@transient
object Gamesave {

  def apply(game: Game) = {
    new Gamesave(game.player, game.score(), game.currentQuestion(), game.numberOfQuestions, Calendar.getInstance().getTime, game.data)
  }
}