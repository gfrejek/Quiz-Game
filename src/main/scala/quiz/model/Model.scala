package quiz.model

import quiz.controller.Controller


case class Player(name: String)

sealed abstract class Category
case class CultureCategory() extends Category
case class SportsCategory() extends Category
case class ScienceCategory() extends Category

object Category {
  val Culture = new CultureCategory()
  val Sports = new SportsCategory()
  val Science = new ScienceCategory()
}

case class Game(player: Player, category: Category)

class Model(controller: Controller) {

}