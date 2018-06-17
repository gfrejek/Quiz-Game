package quiz.view

import quiz.controller.Controller
import quiz.model._
import scalafx.Includes._
import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos, Rectangle2D}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ScrollPane, TextField}
import scalafx.scene.effect.DropShadow
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{GridPane, _}
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Screen
import scalafx.animation.{Timeline, KeyFrame}
import scalafx.scene.chart._
import scalafx.collections.ObservableBuffer
import javafx.scene.{chart => jfxsc}
import scala.language.postfixOps


class View(controller: Controller) {

  val bounds: Rectangle2D = Screen.primary.bounds
  val x_mid: Double = bounds.minX + bounds.width / 2 - 1920 / 2
  val y_mid: Double = bounds.minY + bounds.height / 2 - 1080 / 2


  val smallLogo: Text = new Text {
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


  def refreshPauseScene(): Scene = {
    new Scene() {
      fill = Color.White
      onKeyPressed = (k: KeyEvent) => k.code match {
        case KeyCode.Escape => {
          controller.resumeGame()
          controller.changeScene(gameScene)
        }
        case _ =>
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
            text = "GAME PAUSED"
            alignment = Pos.Center
            style = "-fx-font: normal bold 60pt sans-serif"
            fill = Color.Black
          },
          new Button {
            text = "Save and return to menu"
            prefWidth = 350
            font = Font.font(20)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.gamesaveManager.addGamesave(Gamesave(controller.currentGame))
              controller.changeScene(refreshMenuScene())
            }
          },
          new Button {
            text = "Save and exit"
            prefWidth = 350
            font = Font.font(20)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.gamesaveManager.addGamesave(Gamesave(controller.currentGame))
              controller.closeStage()
            }
          },
          new Button {
            text = "Return to the game"
            prefWidth = 350
            font = Font.font(20)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.resumeGame()
              controller.changeScene(gameScene)
            }
          },
          new Text {
            text = "\n\nLifelines"
            alignment = Pos.Center
            style = "-fx-font: normal bold 30pt sans-serif"
            fill = Color.Black
          },
          new HBox {
            alignment = Pos.Center
            spacing = 50
            padding = Insets(20)
            children = List (
              new Button {
                text = "50:50"
                prefWidth = 300
                font = new Font(20)
                alignment = Pos.Center
                disable = controller.currentGame.fiftyFiftyUsed
                onAction = (e: ActionEvent) => {
                  refreshFiftyFiftyScene()
                }
              },
              new Button {
                text = "Phone-A-Friend"
                prefWidth = 300
                font = new Font(20)
                alignment = Pos.Center
                disable = controller.currentGame.phoneAFriendUsed
                onAction = (e: ActionEvent) => {
                  refreshPhoneAFriendScene()
                }
              },
              new Button {
                text = "Ask the Audience"
                prefWidth = 300
                font = new Font(20)
                alignment = Pos.Center
                disable = controller.currentGame.askTheAudienceUsed
                onAction = (e: ActionEvent) => {
                  refreshAskTheAudienceScene()
                }
              }
            )  
          }
        )
      }
    }
  }


  def refreshPhoneAFriendScene(): Unit = {
    val advice = controller.usePhoneAFriend()

    val text = new Text {
      font = new Font(45)
      fill = Color.Black
      wrappingWidth = 1500
    }
    
    val scene = new Scene {
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
            text = "Contacting a friend..."
            font = new Font(40)
            fill = Color.Black
            alignment = Pos.Center
          },
          text,
          new Button {
            text = "Go back"
            prefWidth = 250
            font = Font.font(20)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshPauseScene())
            }
          }
        )
      }
    }

    controller.changeScene(scene)

    val timeline: Timeline = new Timeline()
    val i = IntegerProperty(0)
    val keyframe: KeyFrame = KeyFrame (
      50 ms,
      onFinished = {
        event: ActionEvent => {
          if (i() > advice.length) {
            timeline.stop()
          } else {
            text.text = advice.substring(0, i())
            i() = i() + 1
          }
        }
      }
    )

    timeline.keyFrames = Seq(keyframe)
    timeline.cycleCount = Timeline.Indefinite

    timeline.play()
  }


  def refreshAskTheAudienceScene(): Unit = {
    val pollRes = controller.useAskTheAudience()
    
    val xAxis = new CategoryAxis {
      label = "Answer"
    }
    val yAxis = new NumberAxis {
      label = "Votes in favour"
    }

    val data = new ObservableBuffer[jfxsc.XYChart.Series[String, Number]]()
    val inFavourOfA = new XYChart.Series[String, Number] {
      name = pollRes(0)._1
    }
    val inFavourOfB = new XYChart.Series[String, Number] {
      name = pollRes(1)._1
    }
    val inFavourOfC = new XYChart.Series[String, Number] {
      name = pollRes(2)._1
    }
    val inFavourOfD = new XYChart.Series[String, Number] {
      name = pollRes(3)._1
    }
    inFavourOfA.data() += XYChart.Data[String, Number]("|", pollRes(0)._2)
    inFavourOfB.data() += XYChart.Data[String, Number]("|", pollRes(1)._2)
    inFavourOfC.data() += XYChart.Data[String, Number]("|", pollRes(2)._2)
    inFavourOfD.data() += XYChart.Data[String, Number]("|", pollRes(3)._2)

    data.addAll(inFavourOfA, inFavourOfB, inFavourOfC, inFavourOfD)

    val barChart = BarChart(xAxis, yAxis)
    barChart.barGap = 0
    barChart.categoryGap = 0
    barChart.title = "Results of the poll"
    barChart.data = data
    
    val scene = new Scene {
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
            text = "Polling the audience..."
            font = new Font(40)
            fill = Color.Black
            alignment = Pos.Center
          },
          barChart,
          new Button {
            text = "Go back"
            prefWidth = 250
            font = Font.font(20)
            alignment = Pos.BottomCenter
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshPauseScene())
            }
          }
        )
      }
    }

    controller.changeScene(scene)
  }


  def refreshFiftyFiftyScene(): Unit = {
    val wrongAnswers = controller.useFiftyFifty()

    def disableIfWrong(button: Button): Unit = {
      for (ans <- wrongAnswers) {
        if(ans == button.text()) {
          button.disable = true
        }
      }
    }

    for(i <- 0 to 3) {
      disableIfWrong(gameScene.root().asInstanceOf[javafx.scene.layout.VBox].children(2).
        asInstanceOf[javafx.scene.layout.GridPane].children(i).asInstanceOf[javafx.scene.control.Button])
    }

    controller.resumeGame()
    controller.changeScene(gameScene)
  }


  lazy val gameScene: Scene = new Scene() {
    fill = Color.White
    onKeyPressed = (k: KeyEvent) => k.code match {
      case KeyCode.Escape => {
        controller.pauseGame()
        controller.changeScene(refreshPauseScene())
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
          val button1: Button = new Button {
            text <== controller.choiceA
            alignmentInParent = Pos.BaselineRight
            prefWidth = 500
            prefHeight = 125
            font = new Font(35)
          }
          val button2: Button = new Button {
            text <== controller.choiceB
            alignmentInParent = Pos.BaselineLeft
            prefWidth = 500
            prefHeight = 125
            font = new Font(35)
          }
          val button3: Button = new Button {
            text <== controller.choiceC
            alignmentInParent = Pos.BaselineRight
            prefWidth = 500
            prefHeight = 125
            font = new Font(35)
          }
          val button4: Button = new Button {
            text <== controller.choiceD
            alignmentInParent = Pos.BaselineLeft
            prefWidth = 500
            prefHeight = 125
            font = new Font(35)
          }

          GridPane.setConstraints(button1, 0, 0)
          GridPane.setConstraints(button2, 0, 1)
          GridPane.setConstraints(button3, 1, 0)
          GridPane.setConstraints(button4, 1, 1)

          padding = Insets(100)

          children ++= Seq(button1, button2, button3, button4)

          button1.onAction = (e: ActionEvent) => {
            if (controller.respondToUserChoice(controller.choiceA())) {
              controller.changeScene(refreshConcludeGameScene())
            }
            enableAllButtons()
          }
          button2.onAction = (e: ActionEvent) => {
            if (controller.respondToUserChoice(controller.choiceB())) {
              controller.changeScene(refreshConcludeGameScene())
            }
            enableAllButtons()
          }
          button3.onAction = (e: ActionEvent) => {
            if (controller.respondToUserChoice(controller.choiceC())) {
              controller.changeScene(refreshConcludeGameScene())
            }
            enableAllButtons()
          }
          button4.onAction = (e: ActionEvent) => {
            if (controller.respondToUserChoice(controller.choiceD())) {
              controller.changeScene(refreshConcludeGameScene())
            }
            enableAllButtons()
          }

          def enableAllButtons() = {
            button1.disable = false
            button2.disable = false
            button3.disable = false
            button4.disable = false
          }
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
        
        lazy val userNameInput: TextField = new TextField() {
          maxWidth = 500
          font = Font(40)
          promptText = "Your Name"
          alignment = Pos.Center
        }
        
        val openTDBButton: Button = new Button {
          text = "General"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        }
        val JServiceButton: Button = new Button {
          text = "JService"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        }
        val numbersAPIButton: Button = new Button {
          text = "Numbers"
          prefWidth = 300
          font = new Font(20)
          alignment = Pos.Center
        }
  
        openTDBButton.onAction = (e: ActionEvent) => {
          choosenSource = QuestionsSource.openTDB
          openTDBButton.disable = true
          JServiceButton.disable = false
          numbersAPIButton.disable = false
          choosenSourcePropertyStr() = "General"
        }
        JServiceButton.onAction = (e: ActionEvent) => {
          choosenSource = QuestionsSource.jService
          openTDBButton.disable = false
          JServiceButton.disable = true
          numbersAPIButton.disable = false
          choosenSourcePropertyStr() = "JService"
        }
        numbersAPIButton.onAction = (e: ActionEvent) => {
          choosenSource = QuestionsSource.numbersAPI
          openTDBButton.disable = false
          JServiceButton.disable = false
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
              JServiceButton,
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
              controller.changeScene(refreshLoadGameScene)
            }
          },
          new Button {
            text = "Leaderboards"
            prefWidth = 300
            font = new Font(20)
            alignment = Pos.Center
            onAction = (e: ActionEvent) => {
              controller.changeScene(refreshHighScoreScene)
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
    
  val openingScene: Scene = new Scene {
    fill = Color.White
    onKeyPressed = (k: KeyEvent) => k.code match {
      case KeyCode.Enter =>
        controller.changeScene(refreshMenuScene())
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
      val scrollPane: ScrollPane = new ScrollPane() {
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
                text = hs.display()
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
      val saveList: ScrollPane = new ScrollPane {
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
                text = save.display()
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