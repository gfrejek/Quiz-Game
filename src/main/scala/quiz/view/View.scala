package quiz.view

import java.util.Calendar

import scalafx.Includes._
import quiz.model._
import quiz.controller.Controller
import scalafx.scene.{Group, Node, Scene}
import scalafx.scene.layout._
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.effect.DropShadow
import scalafx.geometry.Insets
import scalafx.geometry.Pos
import scalafx.stage.{Screen, Stage}
import scalafx.scene.input.{KeyEvent, MouseEvent}
import com.typesafe.config.Config
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.control.{Button, ScrollPane, TextField}
import scalafx.event.ActionEvent
import scalafx.beans.property.StringProperty
import quiz.generators.QuestionGenerator
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.GridPane


class View(model: Model, controller: Controller) {

  val bounds = Screen.primary.bounds
  val x_mid = bounds.minX + bounds.width / 2 - controller.config.getInt("width") / 2
  val y_mid = bounds.minY + bounds.height / 2 - controller.config.getInt("height") / 2


  val smallLogo = new Text {
    text = "-QUIZ-"
    style = "-fx-font: normal bold 100pt sans-serif"
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

  lazy val concludeGameScene: Scene = new Scene() {
    fill = Color.White
    root = new VBox {
      alignment = Pos.Center
      spacing = 200
      padding = Insets(200)
      children = List (
        new Text {
          text = "GAME OVER"
          font = new Font(40)
          alignment = Pos.Center
        },
        new Text {
          text = "Your final score:"
          font = new Font(35)
          alignment = Pos.Center
        },
        new Text {
          text <== controller.scoreStr
          font = new Font(35)
          alignment = Pos.Center
        },
        new Button {
          text = "Return to main menu"
          prefWidth = 500
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => { 
            controller.changeScene(menuScene)
          }
        }
      )
    }
  }
  
  lazy val gameScene: Scene = new Scene() {
    fill = Color.White
    root = new VBox {
      alignment = Pos.Center
      spacing = 200
      padding = Insets(200)
      children = List (
        new HBox {
          alignment = Pos.Center
          spacing = 200
          children = List (
            new Text {
              text = "SCORE: "
              font = new Font(30)
              alignment = Pos.Center
            },
            new Text {
              text <== controller.scoreStr
              font = new Font(30)
              alignment = Pos.Center
            },
            new Text {
              text = "PROGRES (OF 12): "
              font = new Font(30)
              alignment = Pos.Center
            },
            new Text {
              text <== controller.progressStr
              font = new Font(30)
              alignment = Pos.Center
            }
          )
        },
        new Text {
          text <== controller.questionContents
          alignment = Pos.Center
          font = new Font(30)
        },
        new GridPane {
          alignment = Pos.Center
          val button1 = new Button {
            text <== controller.choiceA
            alignmentInParent = Pos.BaselineRight
            prefWidth = 600
            prefHeight = 50
            minHeight = 50
            maxHeight = 50
            font = new Font(25)
            onAction = (e: ActionEvent) => {
              if (controller.respondToUserChoice(controller.choiceA(), 1)) {
                controller.changeScene(concludeGameScene)
              }
            }
          }
          val button2 = new Button {
            text <== controller.choiceB
            alignmentInParent = Pos.BaselineLeft
            prefWidth = 600
            prefHeight = 50
            minHeight = 50
            maxHeight = 50
            font = new Font(25)
            onAction = (e: ActionEvent) => {
              if (controller.respondToUserChoice(controller.choiceB(), 1)) {
                controller.changeScene(concludeGameScene)
              }
            }
          }
          val button3 = new Button {
            text <== controller.choiceC
            alignmentInParent = Pos.BaselineRight
            prefWidth = 600
            prefHeight = 50
            minHeight = 50
            maxHeight = 50
            font = new Font(25)
            onAction = (e: ActionEvent) => {
              if (controller.respondToUserChoice(controller.choiceC(), 1)) {
                controller.changeScene(concludeGameScene)
              }
            }
          }
          val button4 = new Button {
            text <== controller.choiceD
            alignmentInParent = Pos.BaselineLeft
            prefWidth = 600
            prefHeight = 50
            minHeight = 50
            maxHeight = 50
            font = new Font(25)
            onAction = (e: ActionEvent) => {
              if (controller.respondToUserChoice(controller.choiceD(), 1)) {
                controller.changeScene(concludeGameScene)
              }
            }
          }

          GridPane.setConstraints(button1, 0, 0)
          GridPane.setConstraints(button2, 0, 1)
          GridPane.setConstraints(button3, 1, 0)
          GridPane.setConstraints(button4, 1, 1)

          padding = Insets(100)

          children ++= Seq(button1, button2, button3, button4)
        }
      )
    }
  }

  lazy val newGameScene : Scene = new Scene {
    fill = Color.White
    root = new VBox {
      alignment = Pos.Center
      spacing = 40
      padding = Insets(40)

      var choosenSource: QuestionsSource = _
      var choosenSourcePropertyStr = StringProperty("")
      
      lazy val userNameInput = new TextField() {
        maxWidth = 500
        font = Font(40)
        promptText = "Your Name"
        alignment = Pos.Center
      }
      
      val openTDBButton = new Button {
        text = "OpenTDB"
        prefWidth = 300
        font = new Font(20)
        alignment = Pos.Center
      }
      val theSportsDBButton = new Button {
        text = "theSportsDB"
        prefWidth = 300
        font = new Font(20)
        alignment = Pos.Center
      }
      val numbersAPIButton = new Button {
        text = "NumbersAPI"
        prefWidth = 300
        font = new Font(20)
        alignment = Pos.Center
      }

      openTDBButton.onAction = (e: ActionEvent) => {
        choosenSource = QuestionsSource.openTDB
        openTDBButton.disable = true
        theSportsDBButton.disable = false
        numbersAPIButton.disable = false
        choosenSourcePropertyStr() = "OpenTDB"
      }
      theSportsDBButton.onAction = (e: ActionEvent) => {
        choosenSource = QuestionsSource.theSportsDB
        openTDBButton.disable = false
        theSportsDBButton.disable = true
        numbersAPIButton.disable = false
        choosenSourcePropertyStr() = "theSportsDB"
      }
      numbersAPIButton.onAction = (e: ActionEvent) => {
        choosenSource = QuestionsSource.numbersAPI
        openTDBButton.disable = false
        theSportsDBButton.disable = false
        numbersAPIButton.disable = true
        choosenSourcePropertyStr() = "NumbersAPI"
      }

      children = List (
        smallLogo,
        new Text {
          text = "New Game"
          alignment = Pos.Center
          style = "-fx-font: normal bold 50pt sans-serif" 
        },
        new Text {
          text = "Please provide a name and choose a data source"
          alignment = Pos.Center
          style = "-fx-font: normal bold 35pt sans-serif"
        },
        userNameInput,
        new HBox {
          alignment = Pos.Center
          spacing = 100
          padding = Insets(100)
          children = List (
            openTDBButton,
            theSportsDBButton,
            numbersAPIButton
          )
        },
        new Text {
          text = "Choosen data source:"
          alignment = Pos.Center
          font = new Font(15)
        },
        new Text {
          text <== choosenSourcePropertyStr
          alignment = Pos.Center
          font = new Font(15)
        },
        new Button {
          text = "Start Game"
          prefWidth = 400
          font = new Font(25)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => { 
            if (choosenSourcePropertyStr() != "" && userNameInput.text() != "") {
              controller.startNewGame(Player(userNameInput.text()), choosenSource)
              controller.changeScene(gameScene)
            }
          }
        },
        new Button {
          text = "Return to main menu"
          prefWidth = 500
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => { 
            controller.changeScene(menuScene)
          }
        }
      )
    }
  }
    
  val menuScene : Scene = new Scene {
    fill = Color.White
    root = new VBox {
      alignment = Pos.Center
      spacing = 100
      padding = Insets(100)
      children = List (
        new Text {
          text = "-QUIZ-"
          alignment = Pos.Center
          style = "-fx-font: normal bold 100pt sans-serif"
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
            controller.changeScene(newGameScene)
          }
        },
        new Button {
          text = "Load Game"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => {
            controller.gamesaveList() = controller.gamesaveManager.getGamesaveString()
            controller.changeScene(loadGameScene)
          }
        },
        new Button {
          text = "Leaderboards"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => {
            controller.highscoreString() = controller.highscoreManager.getHighscoreString()
            controller.changeScene(leaderboardScene)
          }
        },
        new Button {
          text = "Settings"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => {
            controller.changeScene(settingsScene)
          }
        },
        new Button {
          text = "Quit"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
          onAction = (e: ActionEvent) => { controller.closeStage() }
        }
      )
    }
  }
    
  val openingScene = new Scene {
    fill = Color.White
    onKeyPressed = (k: KeyEvent) => k.code match {
      case KeyCode.Enter => {
        controller.changeScene(menuScene)
      }
      case _ =>
    }
    root = new VBox {
      padding = Insets(50, 80, 50, 80)
      spacing = 100
      children = List (
        new Text {
          text = "-QUIZ-"
          alignment = Pos.Center
          style = "-fx-font: normal bold 100pt sans-serif"
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

  val loadGameScene = new Scene {
    fill = Color.White

    var scrollpane = new ScrollPane() {
      maxWidth = 1500
      prefWidth = 1500
      maxHeight = 800
      prefHeight = 800
      padding = Insets(25)
      content = new Text {
        text <== controller.gamesaveList
        fill = Color.Black
        font = Font.font(40)
      }
    }

    root = new VBox {
      alignment = Pos.Center
      spacing = 100
      padding = Insets(100)
      children = List (
        new Text {
          text = "Saved games"
          alignment = Pos.Center
          style = "-fx-font: normal bold 60pt sans-serif"
          fill = Color.Black
        },
        scrollpane,
        new Button {
          text = "Return to main menu"
          prefWidth = 350
          font = Font.font(25)
          alignment = Pos.BottomCenter
          onAction = (e: ActionEvent) => {
            controller.changeScene(menuScene)
          }
        }
      )
    }
  }


  val leaderboardScene = new Scene {
    fill = Color.White
    val scrollPane = new ScrollPane() {
      maxWidth = 1500
      prefWidth = 1500
      maxHeight = 800
      prefHeight = 800
      padding = Insets(25)
      content = new Text {
          text <== controller.highscoreString
          fill = Color.Black
          font = Font.font(40)
      }
    }

    root = new VBox {
      alignment = Pos.Center
      spacing = 100
      padding = Insets(100)
      children = List (
        new Text {
          text = "Highscores"
          style = "-fx-font: normal bold 60pt sans-serif"
          fill = Color.Black
        },
        scrollPane,
        new Button {
          text = "Return to main menu"
          prefWidth = 350
          font = Font.font(25)
          alignment = Pos.BottomCenter
          onAction = (e: ActionEvent) => {
            controller.changeScene(menuScene)
          }
        }
      )
    }
  }

  val settingsScene = new Scene()

}