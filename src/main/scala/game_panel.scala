package rogue 

import swing._
import swing.event._
import java.awt.{Color,Graphics2D, Graphics}
import java.awt.image.BufferedImage                                           
import java.awt.event.KeyEvent

class GamePanel(main_frame: MainFrame ) extends Panel {
  val matrix_width = 80//96 //80
  val matrix_height = 45//54 //45

  val ui_width = 13//25//9
  val ui_height = 0//13//4

  var dx = 0
  var dy = 0

  val renderer = new Renderer()
  var current_size = renderer.getTileSize()

  var game = new GameObject(matrix_width-ui_width-1,matrix_height-ui_height-1)

  if(!renderer.initIsOk()){
    println("Couldn't load tileset, exiting!!.\n")
    System.exit(1)
  }

  this.preferredSize = new Dimension(matrix_width*(current_size) , matrix_height*(current_size))
  
  override def paintComponent(g : Graphics2D) {
    renderer.drawGame(g, current_size, matrix_width, matrix_height, ui_width, ui_height, game, dx, dy)
  } 
  // LISTENING TO MOUSE AND KEYBOARD
  focusable = true
  listenTo(mouse.clicks)
  listenTo(keys)
  //**************************************************************//
  //************* reactions correspond à la MainLoop *************//
  //**************************************************************//
  this.reactions += {
    case KeyPressed(_,k,_,_) => 
      if(k == Key.P){
        current_size += 1
        this.preferredSize = new Dimension(matrix_width*current_size , matrix_height*current_size)
        this.main_frame.pack()
        this.repaint()
        println("Current resolution : size="+current_size)
      }else if(k == Key.M){
        current_size -= 1
        this.preferredSize = new Dimension(matrix_width*current_size , matrix_height*current_size)
        this.main_frame.pack()
        this.repaint()
        println("Current resolution : size="+current_size)
      }else if(k == Key.Asterisk){
        current_size = renderer.getTileSize()
        this.preferredSize = new Dimension(matrix_width*current_size , matrix_height*current_size)
        this.main_frame.pack()
        this.repaint()
        println("Current resolution : size="+current_size)
      }else if(k == Key.Up){
        if(game.first_floor.getFloor()(game.player.getX())(game.player.getY()-1) != 1 ){
          game.player.setY(game.player.getY()-1)
          this.repaint()
        }
      }else if(k == Key.Down){
        if(game.first_floor.getFloor()(game.player.getX())(game.player.getY()+1) != 1 ){
          game.player.setY(game.player.getY()+1)
          this.repaint()
        }
      }else if(k == Key.Left){
        if(game.first_floor.getFloor()(game.player.getX()-1)(game.player.getY()) != 1 ){
          game.player.setX(game.player.getX()-1)
          this.repaint()
        }
      }else if(k == Key.Right){
        if(game.first_floor.getFloor()(game.player.getX()+1)(game.player.getY()) != 1 ){
          game.player.setX(game.player.getX()+1)
          this.repaint()
        }
      }else if(k == Key.B){
        println("Getting rooms")
        game.getMap().getAllRooms()
        println("Done getting rooms")
        this.repaint()
      }else if(k == Key.N){
        println("Carving tunnel")
        game.getMap().carveOneTunnel()
        println("Done carving")
        this.repaint()
      }else if(k == Key.Z){
        dy -= current_size/2
        this.repaint()
      }else if(k == Key.S){
        dy += current_size/2
        this.repaint()
      }else if(k == Key.Q){
        dx -= current_size/2
        this.repaint()
      }else if(k == Key.D){
        dx += current_size/2
        this.repaint()
      }else if(k == Key.R){
        dx = 0
        dy = 0
        this.repaint()
      }else{
        this.repaint()
        println(k+"  "+ k + "\n")
      }
    case MouseClicked(_,_,_,_,_) => 
      println("regenerating")
      game.newMap()
      this.repaint()
  }
}
