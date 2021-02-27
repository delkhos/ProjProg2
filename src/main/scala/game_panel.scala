package rogue 

import swing._
import swing.event._
import java.awt.{Color,Graphics2D, Graphics}
import java.awt.image.BufferedImage                                           
import java.awt.event.KeyEvent

class GamePanel(main_frame: MainFrame ) extends Panel {
  val game_matrix_dim = new Dimension(70,44)

  val ui_dim = new Dimension(20, 7)

  val screen_matrix_dim = new Dimension(game_matrix_dim.width + ui_dim.width + 1,game_matrix_dim.height + ui_dim.height +1)

  val renderer = new Renderer()

  val vars = new MainLoopVars(game_matrix_dim, renderer.getTileSize())

  var game = new GameObject(game_matrix_dim)

  if(!renderer.initIsOk()){
    println("Couldn't load tileset, exiting!!.\n")
    System.exit(1)
  }

  this.preferredSize = new java.awt.Dimension(screen_matrix_dim.width*renderer.getTileSize() , screen_matrix_dim.height*renderer.getTileSize())
  
  override def paintComponent(g : Graphics2D) {
    renderer.drawGame(g, vars.current_size, screen_matrix_dim, ui_dim, game, vars.dpos)
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
    case e => MainLoopObject.mainLoop(this,vars,renderer, game, e, game_matrix_dim, screen_matrix_dim,ui_dim)
  }
}
