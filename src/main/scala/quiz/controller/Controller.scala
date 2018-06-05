package quiz.controller

import com.typesafe.config.Config
import scalafx.scene.Scene
import scalafx.stage.Stage

class Controller(val config: Config) {

  var currentStage: Stage = _
  
  def passControl(passed: Stage) = {
    currentStage = passed
  }

  def changeScene(scene: Scene) = {
    currentStage.scene = scene
    currentStage.fullScreen = true
  }
  
  def closeStage() = {
    currentStage.close()
  }

}