package quiz

import scalafx.application.JFXApp
import com.typesafe.config.ConfigFactory
import quiz.view.View
import quiz.model.Model
import quiz.controller.Controller

object Application extends JFXApp {
  
  val configuration = ConfigFactory.load("app.conf")
  val controller = new Controller(configuration)
  val model = new Model(controller)
  val view = new View(model)

  stage = new JFXApp.PrimaryStage {
    title = configuration.getString("title")
    minHeight = configuration.getInt("height")
    minWidth = configuration.getInt("width")
    scene = view.mainScene
  }

}