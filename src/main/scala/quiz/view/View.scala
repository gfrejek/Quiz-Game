package quiz.view

import scalafx.Includes._
import quiz.model._
import quiz.controller.Controller
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
import quiz.generators.QuestionGenerator


class View(model: Model, controller: Controller) {

  val bounds = Screen.primary.bounds
  val x_mid = bounds.minX + bounds.width / 2 - controller.config.getInt("width") / 2
  val y_mid = bounds.minY + bounds.height / 2 - controller.config.getInt("height") / 2

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

  // STUB
  lazy val gameScene: Scene = new Scene() {
    fill = Color.White
    root = new VBox {
      alignment = Pos.TopCenter
      // spacing = 150
      // padding = Insets(150)
      children = List (
        new HBox {
          alignment = Pos.TopCenter
          spacing = 700
          padding = Insets(700)
          children = List (
            new HBox {
              alignment = Pos.Center
              children = List (
                new Text {
                  text = "Question "
                  alignment = Pos.Center
                },
                new Text {
                  text <== controller.currentGame.currentQuestion.asString()
                  alignment = Pos.Center
                },
                new Text {
                  text = "/" + controller.currentGame.numberOfQuestions
                  alignment = Pos.Center
                }
              )
            },
            new HBox {
              alignment = Pos.Center
              children = List (
                new Text {
                  text = "Time: 00:05"
                  alignment = Pos.Center
                }
              )
            },
            new HBox {
              alignment = Pos.Center
              children = List (
                new Text {
                  text = "Score: "
                  alignment = Pos.Center
                },
                new Text {
                  text <== controller.currentGame.score.asString()
                  alignment = Pos.Center
                }
              )
            }
          )
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
          text = "Return to Menu"
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
            controller.changeScene(newGameScene)
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
          text = "-----QUIZ-----"
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