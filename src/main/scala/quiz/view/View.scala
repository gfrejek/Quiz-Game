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
            var newGame: Game = Game(new Player("Abcd"), new OpenTDB())
            var newGameSave: Gamesave = Gamesave(newGame)
            controller.gamesaveManager.addGamesave(newGameSave)
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
            controller.highscoreManager.addScore(new Score(50, "abc"))
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
      maxWidth = 1000
      prefWidth = 1000
      maxHeight = 600
      prefHeight = 600
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
          style = "-fx-font: normal bold 80pt sans-serif"
          fill = Color.Black
        },
        scrollpane
        ,
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
      maxWidth = 1000
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
          style = "-fx-font: normal bold 80pt sans-serif"
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