package rogue 

import swing._
import swing.event._
import java.awt.{Color,Graphics2D, Graphics}
import java.awt.image.BufferedImage                                           
import java.awt.event.KeyEvent

class GamePanel(main_frame: MainFrame ) extends Panel {
  var width = 1280
  var height = 720

  val matrix_width = 80//96 //80
  val matrix_height = 45//54 //45

  val ui_width = 9//25//9
  val ui_height = 4//13//4

  val renderer = new Renderer()

  var Game = new GameObject(matrix_width-ui_width-1,matrix_height-ui_height-1)

  if(!renderer.initIsOk()){
    println("Couldn't load tileset, exiting!!.\n")
    System.exit(1)
  }

  this.preferredSize = new Dimension(width, height)


  
  override def paintComponent(g : Graphics2D) {
    renderer.drawGame(g, width, height, matrix_width, matrix_height, ui_width, ui_height, Game.getMap(), Game.getPlayer())
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
        this.width += 10
        this.height = this.width*9/16
        this.preferredSize = new Dimension(this.width, this.height)
        this.main_frame.pack()
        this.repaint()
        println("Current resolution : width="+this.width+" height="+this.height+"\n")
      }else if(k == Key.M){
        this.width -= 10
        this.height = this.width*9/16
        this.preferredSize = new Dimension(this.width, this.height)
        this.main_frame.pack()
        this.repaint()
        println("Current resolution : width="+this.width+" height="+this.height+"\n")
      }else if(k == Key.Asterisk){
        this.width = 1280
        this.height = 720
        this.preferredSize = new Dimension(this.width, this.height)
        this.main_frame.pack()
        this.repaint()
        println("Current resolution : width="+this.width+" height="+this.height+"\n")
      }else if(k == Key.Up){
        Game.player.setY(Game.player.getY()-1)
        this.repaint()
      }else if(k == Key.Down){
        Game.player.setY(Game.player.getY()+1)
        this.repaint()
      }else if(k == Key.Left){
        Game.player.setX(Game.player.getX()-1)
        this.repaint()
      }else if(k == Key.Right){
        Game.player.setX(Game.player.getX()+1)
        this.repaint()
      }else if(k == Key.B){
        println("Getting rooms")
        Game.getMap().getAllRooms()
        println("Done getting rooms")
        this.repaint()
      }else if(k == Key.N){
        println("Carving tunnel")
        Game.getMap().carveOneTunnel()
        println("Done carving")
        this.repaint()
      }else{
        println(k+"  "+ k + "\n")
      }
    case MouseClicked(_,_,_,_,_) => 
      //first_floor = new MapPolygon(70,40, 3+r.nextInt(8), 3+r.nextInt(5), r.nextInt(360).toDouble)
      //first_floor = new MapPolygon(70,40)
      println("regenerating")
      Game.newMap()
      //first_floor.oneGen()
      this.repaint()
      //println("map size : " + tileset_handler.getMapSize)
  }
}
