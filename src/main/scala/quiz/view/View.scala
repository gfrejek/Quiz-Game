package quiz.view

import quiz.model.Model
import scalafx.scene.Scene
import scalafx.scene.layout.VBox

class View(model: Model) {

    val mainScene = new Scene { root = new VBox() }

}