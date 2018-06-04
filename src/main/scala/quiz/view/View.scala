package quiz.view

import scalafx.Includes._
import quiz.model._
import scalafx.scene.Scene
import scalafx.scene.layout.{VBox, HBox}
import scalafx.scene.text.Text
import scalafx.scene.text.Font
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.effect.DropShadow
import scalafx.geometry.Insets
import scalafx.geometry.Pos
import scalafx.stage.{Stage, Screen}
import scalafx.scene.input.{KeyEvent, MouseEvent}
import com.typesafe.config.Config
import scalafx.scene.input.{KeyCodeCombination, KeyCode, KeyCombination}
import scalafx.scene.control.{Button, TextField}
import scalafx.event.ActionEvent
import scalafx.beans.property.StringProperty


class View(model: Model, configuration: Config) {

  var primaryStage: Stage = _
  val bounds = Screen.primary.bounds
  val x_mid = bounds.minX + bounds.width / 2 - configuration.getInt("width") / 2
  val y_mid = bounds.minY + bounds.height / 2 - configuration.getInt("height") / 2

  def passStage(passed: Stage) = {
    primaryStage = passed
  }

  val smallLogo = new Text {
    text = "-----QUIZ-----"
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
  }

  val gameScene = new Scene()

  lazy val newGameScene : Scene = new Scene {
    fill = Color.White
    root = new VBox {
      alignment = Pos.Center
      spacing = 40
      padding = Insets(40)

      var choosenCategory: Category = _
      var choosenCategoryPropertyStr = StringProperty("")
      var choosenName: String = _
      
      lazy val userNameInput = new TextField() {
        promptText = "Your Name"
        alignment = Pos.Center
      }
      
      val cultButton = new Button {
        text = "Culture"
        prefWidth = 300
        font = new Font(20)
        alignment = Pos.Center
      }
      val scienceButton = new Button {
        text = "Science"
        prefWidth = 300
        font = new Font(20)
        alignment = Pos.Center
      }
      val sportsButton = new Button {
        text = "Sports"
        prefWidth = 300
        font = new Font(20)
        alignment = Pos.Center
      }

      cultButton.onAction = (e: ActionEvent) => {
        choosenCategory = Category.Culture
        cultButton.disable = true
        sportsButton.disable = false
        scienceButton.disable = false
        choosenCategoryPropertyStr() = "Culture"
      }
      sportsButton.onAction = (e: ActionEvent) => {
        choosenCategory = Category.Sports
        cultButton.disable = false
        sportsButton.disable = true
        scienceButton.disable = false
        choosenCategoryPropertyStr() = "Sports"
      }
      scienceButton.onAction = (e: ActionEvent) => {
        choosenCategory = Category.Science
        cultButton.disable = false
        sportsButton.disable = false
        scienceButton.disable = true
        choosenCategoryPropertyStr() = "Science"
      }

      children = List (
        smallLogo,
        new Text {
          text = "New Game"
          alignment = Pos.Center
          style = "-fx-font: normal bold 50pt sans-serif" 
        },
        new Text {
          text = "Please provide a name and choose a category"
          alignment = Pos.Center
          style = "-fx-font: normal bold 35pt sans-serif"
        },
        userNameInput,
        new HBox {
          alignment = Pos.Center
          spacing = 100
          padding = Insets(100)
          children = List (
            cultButton,
            sportsButton,
            scienceButton
          )
        },
        new Text {
          text = "Choosen category:"
          alignment = Pos.Center
          font = new Font(15)
        },
        new Text {
          text <== choosenCategoryPropertyStr
          alignment = Pos.Center
          font = new Font(15)
        },
        new Button {
          text = "Start Game"
          prefWidth = 400
          font = new Font(25)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => { 
            if (choosenCategoryPropertyStr() != "" && userNameInput.text() != "") {
              val newGame = Game(Player(userNameInput.text()), choosenCategory)
              primaryStage.scene = gameScene
              primaryStage.fullScreen = true
            }
          }
        },
        new Button {
          text = "Return to Menu"
          prefWidth = 500
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => { 
            primaryStage.scene = menuScene
            primaryStage.fullScreen = true
          }
        }
      )
    }
  }
    
  val menuScene = new Scene {
    fill = Color.White
    root = new VBox {
      alignment = Pos.Center
      spacing = 100
      padding = Insets(100)
      children = List (
        new Text {
          text = "-----QUIZ-----"
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
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => { 
            primaryStage.scene = newGameScene
            primaryStage.fullScreen = true
          }
        },
        new Button {
          text = "Load Game"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        },
        new Button {
          text = "Leaderboards"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        },
        new Button {
          text = "Settings"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        },
        new Button {
          text = "Quit"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => { primaryStage.close() }
        }
      )
    }
  }
    
  val openingScene = new Scene {
    fill = Color.White
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
          fill = Color.Black
          alignment = Pos.Center
        }
      )
    }
  }

  val continueGameScene = new Scene()
  val leaderboardScene = new Scene()
  val settingScene = new Scene()

}