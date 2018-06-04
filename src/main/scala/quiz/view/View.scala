package quiz.view

import scalafx.Includes._
import quiz.model.Model
import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import scalafx.scene.control.Label
import scalafx.scene.text.Text
import scalafx.scene.text.Font
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.effect.DropShadow
import scalafx.geometry.Insets
import scalafx.scene.layout.Background
import scalafx.scene.layout.BackgroundFill
import scalafx.scene.layout.CornerRadii
import scalafx.geometry.Pos
import scalafx.stage.{Stage, Screen}
import scalafx.scene.input.{KeyEvent, MouseEvent}
import com.typesafe.config.Config
import scalafx.scene.input.{KeyCodeCombination, KeyCode, KeyCombination}
import scalafx.animation.FadeTransition
import scalafx.scene.control.Button


class View(model: Model, configuration: Config) {

    var primaryStage: Stage = _
    val bounds = Screen.primary.bounds
    val x_mid = bounds.minX + bounds.width / 2 - configuration.getInt("width") / 2
    val y_mid = bounds.minY + bounds.height / 2 - configuration.getInt("height") / 2

    def passStage(passed: Stage) = {
        primaryStage = passed
    }
    
    val menuScene = new Scene {
        fill = Color.Azure
        root = new VBox {
            spacing = 100
            padding = Insets(100)
            children = List (
               new Text {
                    text = "QUIZ"
                    alignment = Pos.Center
                    style = "-fx-font: normal bold 75pt sans-serif"
                    fill = new LinearGradient (
                        endX = 0,
                        stops = Stops(White, DarkGray)
                    )
                    effect = new DropShadow {
                        color = DarkGray
                        radius = 15
                        spread = 0.25
                    }
                }, 
                new Button {
                    text = "New Game"
                    font = new Font(20)
                    alignment = Pos.Center
                    onMouseClicked = (m: MouseEvent) => m.primaryButtonDown match {
                        case true =>
                        case false =>
                    }
                }
            )
        }
    }
    
    val openingScene = new Scene {
        fill = Color.Azure
        onKeyPressed = (k: KeyEvent) => k.code match {
            case KeyCode.Enter => {
                primaryStage.scene = menuScene
                primaryStage.fullScreen = true
            }
            case _ =>
        }
        root = new VBox {
            padding = Insets(50, 80, 50, 80)
            children = List (
                new Text {
                    text = "QUIZ"
                    alignment = Pos.Center
                    style = "-fx-font: normal bold 150pt sans-serif"
                    fill = new LinearGradient (
                        endX = 0,
                        stops = Stops(White, DarkGray)
                    )
                    effect = new DropShadow {
                        color = DarkGray
                        radius = 15
                        spread = 0.25
                    }
                },
                new Text {
                    text = "Press ENTER to continue"
                    fill = Color.White
                    alignment = Pos.Center
                }
            )
        }
    }

    val newGameScene = new Scene()
    val continueGameScene = new Scene()
    val leaderboardScene = new Scene()
    val settingScene = new Scene()
    val gameScene = new Scene()

}