package rogue 

import swing._
import swing.event._
import java.awt.{Color,Graphics2D, Graphics}
import java.awt.image.BufferedImage                                           
import java.awt.event.KeyEvent

class GamePanel(main_frame: MainFrame ) extends Panel {
  val game_matrix_width = 70
  val game_matrix_height = 44

  val ui_width = 13//25//9
  val ui_height = 5//13//4

  val screen_matrix_width = game_matrix_width + ui_width + 1//96 //80
  val screen_matrix_height = game_matrix_height + ui_height +1 //45

  var dx = 0.0
  var dy = 0.0
  var sx = 0.0
  var sy = 0.0
  var posx_center = game_matrix_width/2.0
  var posy_center = game_matrix_height/2.0
  var grabbed = false

  val renderer = new Renderer()
  var current_size = renderer.getTileSize()

  var game = new GameObject(game_matrix_width,game_matrix_height)

  if(!renderer.initIsOk()){
    println("Couldn't load tileset, exiting!!.\n")
    System.exit(1)
  }

  this.preferredSize = new Dimension(screen_matrix_width*(current_size) , screen_matrix_height*(current_size))
  
  override def paintComponent(g : Graphics2D) {
    renderer.drawGame(g, current_size, screen_matrix_width, screen_matrix_height, ui_width, ui_height, game, dx.toInt, dy.toInt)
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
    case KeyPressed(_,k,_,_) => 
      if(k == Key.Asterisk){
        current_size = renderer.getTileSize()
        dx = 0
        dy = 0
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
    case MousePressed(_,coord,_,_,_) => 
      println("clicked")
      grabbed = true
      //sx = coord.getX()
      //sy = coord.getY()
      //println("regenerating")
      //game.newMap()
      //this.repaint()
    case MouseDragged(_,coord,_) => 
        val sx2 = coord.getX()
        val sy2 = coord.getY()
        println("resolving DRAG")
        if(grabbed){
          println("first grab OK")
          sx = sx2
          sy = sy2
          grabbed = false
        }
        dx += (sx2-sx) 
        dy += (sy2-sy)
        println("sx2= "+sx2.toInt+"  sy2="+sy2.toInt)
        println("sx= "+sx.toInt+"  sy="+sy.toInt)
        println("dragging dsx= "+(sx2-sx).toInt+"  "+(sy2-sy).toInt)
        sx = sx2
        sy = sy2
        posx_center = ((renderer.getTileSize()*game_matrix_width/2 -dx)/(current_size.toFloat))
        posy_center = ((renderer.getTileSize()*game_matrix_height/2 - dy )/(current_size.toFloat))
        this.repaint()
    case MouseReleased(_,coord,_,_,_) => 
      grabbed = false
      println("La position centrale= "+((renderer.getTileSize()*game_matrix_width/2 -dx)/(current_size.toFloat))+" "+((renderer.getTileSize()*game_matrix_height/2 - dy )/(current_size.toFloat)))
    case MouseWheelMoved(_,coord,_,n) =>
      val old = current_size
      current_size += n
      if(current_size<renderer.getTileSize()){
        current_size = renderer.getTileSize()
      }else{
        dx -= n* posx_center
        dy -= n* posy_center
        //dx -= n*game_matrix_width/2
        //dy -= n*game_matrix_height/2
      }
      this.repaint()
      println("Current resolution : size="+current_size)
      //println("La molette a bougé de : "+n + " position = "+(coord.getX()/renderer.getTileSize()).toInt+" "+(coord.getY()/renderer.getTileSize()).toInt)
  }
}
