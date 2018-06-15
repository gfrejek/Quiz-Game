package quiz

import scalafx.application.JFXApp
import com.typesafe.config.ConfigFactory
import quiz.view.View
import quiz.model.Model
import quiz.controller.Controller
import scalafx.stage.Screen
import scalafx.scene.input.{KeyCodeCombination, KeyCode, KeyCombination}

object Application extends JFXApp {
  
  val configuration = ConfigFactory.load("app.conf")
  val controller = new Controller(configuration)
  val model = new Model(controller)
  val view = new View(model, controller)
  val bounds = Screen.primary.bounds

  stage = new JFXApp.PrimaryStage {
    title = configuration.getString("title")
    minHeight = 1080
    minWidth = 1920
    x = bounds.minX + bounds.width / 2 - 1920 / 2
    y = bounds.minY + bounds.height / 2 - 1080 / 2
    fullScreenExitHint = ""
    fullScreenExitKey = new KeyCodeCombination(KeyCode.X, KeyCombination.ControlDown)
    alwaysOnTop = true
    fullScreen = true
  }

  controller.passControl(stage)
  controller.changeScene(view.openingScene)

}