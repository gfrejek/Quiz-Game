package quiz

import scalafx.application.JFXApp
import com.typesafe.config.ConfigFactory

object QuizGameApp extends JFXApp {
  
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