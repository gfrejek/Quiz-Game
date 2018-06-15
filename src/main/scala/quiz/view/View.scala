package quiz.view

import quiz.controller.Controller
import quiz.model._
import scalafx.Includes._
import scalafx.beans.property.StringProperty
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ScrollPane, TextField}
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{GridPane, _}
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Screen


class View(model: Model, controller: Controller) {

  val bounds = Screen.primary.bounds
  val x_mid = bounds.minX + bounds.width / 2 - 1920 / 2
  val y_mid = bounds.minY + bounds.height / 2 - 1080 / 2


  val smallLogo = new Text {
    text = "-QUIZ-"
    style = "-fx-font: normal bold 110pt sans-serif"
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
        padding = Insets(200)
        children = List (
          smallLogo,
          new Text {
            text = "GAME OVER"
            font = new Font(75)
            alignment = Pos.Center
          },
          new Text {
            text = "Your final score: " + controller.scoreStr()
            font = new Font(40)
            alignment = Pos.Center
          },
          new Button {
            text = "Return to main menu"
            prefWidth = 500
            font = new Font(30)
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
        controller.currentTimerTask.cancel()
        controller.timer.purge()
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
      padding = Insets(150, 100, 50, 100)
      children = List (
        new HBox {
          alignment = Pos.Center
          spacing = 150
          children = List (
            new Text {
              text = "SCORE: "
              font = new Font(50)
              alignment = Pos.Center
            },
            new Text {
              text <== controller.scoreStr
              font = new Font(50)
              alignment = Pos.Center
            },
            new Text {
              text = "QUESTION: "
              font = new Font(50)
              alignment = Pos.Center
            },
            new Text {
              text <== controller.progressStr + "/" + controller.currentGame.numberOfQuestions.toString
              font = new Font(50)
              alignment = Pos.Center
            },
            new Text {
              text = "Time: "
              font = new Font(50)
              alignment = Pos.BottomCenter
            },
            new Text {
              text <== controller.clockString
              font = new Font(50)
              alignment = Pos.BottomCenter
            }
          )
        },
        new Text {
          text <== controller.questionContents
          alignment = Pos.Center
          wrappingWidth = 1200
          font = new Font(55)
        },
        new GridPane {
          alignment = Pos.Center
          padding = Insets(50)
          val button1 = new Button {
            text <== controller.choiceA
            alignmentInParent = Pos.BaselineRight
            prefWidth = 500
            prefHeight = 125
            font = new Font(35)
            onAction = (e: ActionEvent) => {
              if (controller.respondToUserChoice(controller.choiceA(), 1)) {
                controller.changeScene(refreshConcludeGameScene())
              }
            }
          }
          val button2 = new Button {
            text <== controller.choiceB
            alignmentInParent = Pos.BaselineLeft
            prefWidth = 500
            prefHeight = 125
            font = new Font(35)
            onAction = (e: ActionEvent) => {
              if (controller.respondToUserChoice(controller.choiceB(), 1)) {
                controller.changeScene(refreshConcludeGameScene())
              }
            }
          }
          val button3 = new Button {
            text <== controller.choiceC
            alignmentInParent = Pos.BaselineRight
            prefWidth = 500
            prefHeight = 125
            font = new Font(35)
            onAction = (e: ActionEvent) => {
              if (controller.respondToUserChoice(controller.choiceC(), 1)) {
                controller.changeScene(refreshConcludeGameScene())
              }
            }
          }
          val button4 = new Button {
            text <== controller.choiceD
            alignmentInParent = Pos.BaselineLeft
            prefWidth = 500
            prefHeight = 125
            font = new Font(35)
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
        spacing = 25
        padding = Insets(150)
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
            style = "-fx-font: normal bold 75pt sans-serif"
          },
          new Text {
            text = "Please provide a name and choose a category"
            alignment = Pos.Center
            style = "-fx-font: normal 30pt sans-serif"
          },
          userNameInput,
          new HBox {
            alignment = Pos.Center
            spacing = 50
            padding = Insets(20)
            children = List (
              openTDBButton,
              theSportsDBButton,
              numbersAPIButton
            )
          },
          new Text {
            text = "Chosen category:"
            alignment = Pos.Center
            font = new Font(30)
          },
          new Text {
            text <== choosenSourcePropertyStr
            alignment = Pos.Center
            font = new Font(30)
          },
          new Button {
            text = "Start Game"
            prefWidth = 400
            font = new Font(40)
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
            font = new Font(25)
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
        spacing = 50
        padding = Insets(150)
        children = List (
          new Text {
            text = "-QUIZ-"
            alignment = Pos.Center
            style = "-fx-font: normal bold 140pt sans-serif"
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
          style = "-fx-font: normal bold 200pt sans-serif"
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
          font = Font(50)
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
      var iteration: Int = 0
      val scrollPane = new ScrollPane() {
        maxWidth = 1000
        prefWidth = 1000
        maxHeight = 1100
        prefHeight = 1100
        padding = Insets(20)
        content = new VBox {
          prefWidth = 950
          alignment = Pos.Center
          children = {
            for (hs <- controller.highscoreManager.scoreList) yield
              new Text {
                text = hs.display
                font = Font.font("Courier New", 49)
                wrappingWidth = 950
                alignment = Pos.Center
                fill = iteration match{
                  case 0 => Color.Gold
                  case 1 => Color.Silver
                  case 2 => Color.Brown
                  case _ => Color.Black
                }
                iteration += 1
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
        spacing = 50
        padding = Insets(100)
        children = List (
          smallLogo,
          new Text {
            text = "Highscores"
            style = "-fx-font: normal bold 60pt sans-serif"
            fill = Color.Black
          },
          scrollPane,
          new Button {
            text = "Return to main menu"
            prefWidth = 350
            font = Font.font(20)
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
        spacing = 50
        padding = Insets(100)
        children = List (
          smallLogo,
          new Text {
            text = "Settings"
            alignment = Pos.Center
            style = "-fx-font: normal bold 60pt sans-serif"
            fill = Color.Black
          },
          new Button {
            text = "Erase all saved games"
            prefWidth = 350
            font = Font.font(20)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.gamesaveManager.reset()
            }
          },
          new Button {
            text = "Reset highscores"
            prefWidth = 350
            font = Font.font(20)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.highscoreManager.reset()
            }
          },
          new Button {
            text = "Return to main menu"
            prefWidth = 350
            font = Font.font(20)
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
        maxWidth = 1200
        prefWidth = 1200
        maxHeight = 800
        prefHeight = 800
        padding = Insets(15)
        content = new VBox {
          maxWidth = 1200 - 30
          children = for (save <- controller.gamesaveManager.gamesaveList) yield
          new HBox {
            spacing = 50
            maxWidth = 1200 - 30
            maxHeight = 70
            children = List (
              new Text {
                text = save.display
                font = Font.font("Courier New", 46)
                prefHeight = 70
                prefWidth = 1200 - 450 - 50 - 30
                wrappingWidth = 1200 - 450 - 50 - 30
                alignment = Pos.CenterLeft
                alignmentInParent = Pos.CenterLeft
              },
              new HBox {
                alignmentInParent = Pos.CenterRight
                alignment = Pos.CenterRight
                minWidth = 450
                maxWidth = 450
                spacing = 10
                children = List (
                  new Button {
                    text = "LOAD"
                    font = Font(43)
                    alignmentInParent = Pos.CenterRight
                    alignment = Pos.Center
                    prefWidth = 220
                    prefHeight = 55
                    onAction = (e: ActionEvent) => {
                      val game = save.toGame
                      controller.continueGame(game)
                      controller.gamesaveManager.deleteGamesave(save)
                      controller.changeScene(gameScene)
                    }
                  },
                  new Button {
                    text = "DEL"
                    font = Font(45)
                    alignmentInParent = Pos.CenterRight
                    alignment = Pos.Center
                    prefWidth = 220
                    prefHeight = 55
                    onAction = (e: ActionEvent) => {
                      controller.gamesaveManager.deleteGamesave(save)
                      controller.changeScene(refreshLoadGameScene())
                    }
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
        spacing = 40
        padding = Insets(100)
        children = List (
          smallLogo,
          new Text {
            text = "Saved games"
            alignment = Pos.Center
            style = "-fx-font: normal bold 60pt sans-serif"
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