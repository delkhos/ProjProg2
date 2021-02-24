package rogue 

import swing._
import swing.event._
import java.awt.{Color,Graphics2D, Graphics}
import java.awt.image.BufferedImage                                           
import java.awt.event.KeyEvent

class GamePanel(main_frame: MainFrame ) extends Panel {
  val game_matrix_width = 70
  val game_matrix_height = 44

  val ui_width = 13 //25//9
  val ui_height = 5 //13//4

  val screen_matrix_width = game_matrix_width + ui_width + 1//96 //80
  val screen_matrix_height = game_matrix_height + ui_height +1 //45

  val renderer = new Renderer()

  val vars = new MainLoopVars(game_matrix_width, game_matrix_height, renderer.getTileSize())

  var game = new GameObject(game_matrix_width,game_matrix_height)

  if(!renderer.initIsOk()){
    println("Couldn't load tileset, exiting!!.\n")
    System.exit(1)
  }

  this.preferredSize = new Dimension(screen_matrix_width*renderer.getTileSize() , screen_matrix_height*renderer.getTileSize())
  
  override def paintComponent(g : Graphics2D) {
    renderer.drawGame(g, vars.getCURRENT_SIZE(), screen_matrix_width, screen_matrix_height, ui_width, ui_height, game, vars.getDX().toInt, vars.getDY().toInt)
  } 
  // LISTENING TO MOUSE AND KEYBOARD
  focusable = true
  listenTo(mouse.clicks)
  listenTo(mouse.moves)
  listenTo(mouse.wheel)
  listenTo(keys)
  //**************************************************************//
  //************* reactions correspond à la MainLoop *************//
  //**************************************************************//
  this.reactions += {
    case e => MainLoopObject.mainLoop(this,vars,renderer, game, e, game_matrix_width, game_matrix_height)
  }
}
