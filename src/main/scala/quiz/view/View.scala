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
import scalafx.scene.control.ListView
import scalafx.collections.ObservableBuffer


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

  def refreshConcludeGameScene(): Scene = {
    new Scene() {
      fill = Color.White
      root = new VBox {
        maxWidth = 1920
        prefWidth = 1920
        maxHeight = 1080
        prefHeight = 1080
        alignment = Pos.Center
        spacing = 150
        padding = Insets(150)
        children = List (
          smallLogo,
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
              controller.changeScene(refreshMenuScene())
            }
          }
        )
      }
    }
  }
  
  lazy val gameScene: Scene = new Scene() {
    fill = Color.White
    onKeyPressed = (k: KeyEvent) => k.code match {
      case KeyCode.Escape => {
        controller.gamesaveManager.addGamesave(Gamesave(controller.currentGame))
        controller.changeScene(refreshMenuScene())
      }
      case _ =>
    }
    root = new VBox {
      maxWidth = 1920
      prefWidth = 1920
      maxHeight = 1080
      prefHeight = 1080
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
                controller.changeScene(refreshConcludeGameScene())
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
                controller.changeScene(refreshConcludeGameScene())
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
                controller.changeScene(refreshConcludeGameScene())
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
                controller.changeScene(refreshConcludeGameScene())
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

  def refreshNewGameScene(): Scene = {
    new Scene {
      fill = Color.White
      root = new VBox {
        alignment = Pos.Center
        spacing = 40
        padding = Insets(40)
        maxWidth = 1920
        prefWidth = 1920
        maxHeight = 1080
        prefHeight = 1080
  
        var choosenSource: QuestionsSource = _
        var choosenSourcePropertyStr = StringProperty("")
        
        lazy val userNameInput = new TextField() {
          maxWidth = 500
          font = Font(40)
          promptText = "Your Name"
          alignment = Pos.Center
        }
        
        val openTDBButton = new Button {
          text = "General"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        }
        val theSportsDBButton = new Button {
          text = "Sports"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        }
        val numbersAPIButton = new Button {
          text = "Numbers"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        }
  
        openTDBButton.onAction = (e: ActionEvent) => {
          choosenSource = QuestionsSource.openTDB
          openTDBButton.disable = true
          theSportsDBButton.disable = false
          numbersAPIButton.disable = false
          choosenSourcePropertyStr() = "General"
        }
        theSportsDBButton.onAction = (e: ActionEvent) => {
          choosenSource = QuestionsSource.theSportsDB
          openTDBButton.disable = false
          theSportsDBButton.disable = true
          numbersAPIButton.disable = false
          choosenSourcePropertyStr() = "Sports"
        }
        numbersAPIButton.onAction = (e: ActionEvent) => {
          choosenSource = QuestionsSource.numbersAPI
          openTDBButton.disable = false
          theSportsDBButton.disable = false
          numbersAPIButton.disable = true
          choosenSourcePropertyStr() = "Numbers"
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
              openTDBButton,
              theSportsDBButton,
              numbersAPIButton
            )
          },
          new Text {
            text = "Choosen category:"
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
              controller.changeScene(refreshMenuScene())
            }
          }
        )
      }
    }
  }
    
  def refreshMenuScene(): Scene = {
    new Scene {
      fill = Color.White
      root = new VBox {
        maxWidth = 1920
        prefWidth = 1920
        maxHeight = 1080
        prefHeight = 1080
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
              controller.changeScene(refreshNewGameScene())
            }
          },
          new Button {
            text = "Load Game"
            prefWidth = 300
            font = new Font(20)
            alignment = Pos.Center
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshLoadGameScene())
            }
          },
          new Button {
            text = "Leaderboards"
            prefWidth = 300
            font = new Font(20)
            alignment = Pos.Center
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshHighScoreScene())
            }
          },
          new Button {
            text = "Settings"
            prefWidth = 300
            font = new Font(20)
            alignment = Pos.Center
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshSettingsScene())
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
  }
    
  val openingScene = new Scene {
    fill = Color.White
    onKeyPressed = (k: KeyEvent) => k.code match {
      case KeyCode.Enter => {
        controller.changeScene(refreshMenuScene())
      }
      case _ =>
    }
    root = new VBox {
      maxWidth = 1920
      prefWidth = 1920
      maxHeight = 1080
      prefHeight = 1080
      padding = Insets(100)
      spacing = 100
      alignment = Pos.Center
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

  def refreshHighScoreScene(): Scene = {
    controller.highscoreManager.loadScoreFile()
    
    new Scene {
      fill = Color.White
      val scrollPane = new ScrollPane() {
        maxWidth = 800
        prefWidth = 800
        maxHeight = 800
        prefHeight = 800
        padding = Insets(25)
        content = new VBox {
          alignment = Pos.Center
          children = {
            for (hs <- controller.highscoreManager.scoreList) yield
              new Text {
                text = hs.display
                font = new Font(20)
                wrappingWidth = 700
                alignment = Pos.Center
              }
          }
        }
      }
  
      root = new VBox {
        maxWidth = 1920
        prefWidth = 1920
        maxHeight = 1080
        prefHeight = 1080
        alignment = Pos.Center
        spacing = 60
        padding = Insets(60)
        children = List (
          smallLogo,
          new Text {
            text = "Highscores"
            style = "-fx-font: normal bold 50pt sans-serif"
            fill = Color.Black
          },
          scrollPane,
          new Button {
            text = "Return to main menu"
            prefWidth = 350
            font = Font.font(25)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshMenuScene())
            }
          }
        )
      }
    }
  }

  def refreshSettingsScene(): Scene = {
    new Scene {
      fill = Color.White
      root = new VBox {
        maxWidth = 1920
        prefWidth = 1920
        maxHeight = 1080
        prefHeight = 1080
        alignment = Pos.Center
        spacing = 100
        padding = Insets(100)
        children = List (
          smallLogo,
          new Text {
            text = "Settings"
            alignment = Pos.Center
            style = "-fx-font: normal bold 50pt sans-serif"
            fill = Color.Black
          },
          new Button {
            text = "Erase All Saves"
            prefWidth = 350
            font = Font.font(25)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.gamesaveManager.reset()
            }
          },
          new Button {
            text = "Reset HighScores"
            prefWidth = 350
            font = Font.font(25)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.highscoreManager.reset()
            }
          },
          new Button {
            text = "Return to main menu"
            prefWidth = 350
            font = Font.font(25)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshMenuScene())
            }
          }
        )
      }
    }
  }

  def refreshLoadGameScene(): Scene = {
    controller.gamesaveManager.loadGamesaveFile()

    new Scene {
      fill = Color.White
      val saveList = new ScrollPane {
        maxWidth = 1500
        prefWidth = 1500
        maxHeight = 800
        prefHeight = 800
        padding = Insets(25)
        content = new VBox {children = for (save <- controller.gamesaveManager.gamesaveList) yield
          new HBox {
            spacing = 500
            children = List (
              new Text {
                text = save.display
                wrappingWidth = 700
                alignment = Pos.CenterLeft
                alignmentInParent = Pos.CenterLeft
              },
              new HBox {
                alignmentInParent = Pos.CenterRight
                alignment = Pos.CenterRight
                spacing = 20
                children = List (
                  new Button {
                    text = "LOAD"
                    alignmentInParent = Pos.CenterRight
                    alignment = Pos.Center
                    prefWidth = 100
                    onAction = (e: ActionEvent) => {
                      val game = save.toGame
                      controller.continueGame(game)
                      controller.changeScene(gameScene)
                    }
                  },
                  new Button {
                    text = "DELETE"
                    alignmentInParent = Pos.CenterRight
                    alignment = Pos.Center
                    prefWidth = 100
                  }
                )
              }
            )
          }
        }
      }

      root = new VBox {
        maxWidth = 1920
        prefWidth = 1920
        maxHeight = 1080
        prefHeight = 1080
        alignment = Pos.Center
        spacing = 60
        padding = Insets(60)
        children = List (
          smallLogo,
          new Text {
            text = "Saved games"
            alignment = Pos.Center
            style = "-fx-font: normal bold 50pt sans-serif"
            fill = Color.Black
          },
          saveList,
          new Button {
            text = "Return to main menu"
            prefWidth = 350
            font = Font.font(25)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshMenuScene())
            }
          }
        )
      }
    }
  }

}