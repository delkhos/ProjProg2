package rogue 

import swing._
import swing.event._
import java.awt.{Color,Graphics2D, Graphics}
import java.awt.image.BufferedImage                                           
import java.awt.event.KeyEvent

class GamePanel(main_frame: MainFrame ) extends Panel {
  var width = 1280
  var height = 720

  val matrix_width = 80
  val matrix_height = 45

  var px = 1
  var py = 1

  val tileset_handler = new TileSetHandler(16, "src/main/resources/3_16.png")

  this.preferredSize = new Dimension(width, height)

  val slime = new Environment(40, 23, new Sprite( Array[SubSprite](new SubSprite(9,"000000255"), new SubSprite(47, "255255255")) , Color.BLACK), true)

  def paintCharacter(g: Graphics2D,c: Int, px: Int, py: Int, bg: Color, fg: String){
    val x:Int = c % 16 
    val y:Int = c / 16 
    val dx1:Int = x*tileset_handler.getSize() 
    val dy1:Int = y*tileset_handler.getSize()
    val dx2:Int = dx1+(tileset_handler.getSize()-1)
    val dy2:Int = dy1+(tileset_handler.getSize()-1)
    val size:Float = width.toFloat/matrix_width.toFloat
    val sx1:Float = (px-1)*size
    val sy1:Float = (py-1)*size
    val sx2:Float = sx1 + (size-1)
    val sy2:Float = sy1 + (size-1)
    g.setColor(bg);
    g.fillRect(sx1.toInt,sy1.toInt, tileset_handler.getSize(), tileset_handler.getSize())
    g.drawImage(tileset_handler.getColoredTileset(fg), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)
  }
  
  override def paintComponent(g : Graphics2D) {
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)
    g.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED)

    g.setColor(Color.BLACK);
    g.fillRect(0,0, width, height)

    if (tileset_handler.isReady()){
      /*
      val r = scala.util.Random
      for(i <- 1 to 80 ){
        for(j <- 1 to 45 ){
          val c = r.nextInt(256)
          val red = "%03d".format(r.nextInt(11) * 25)
          val green = "%03d".format(r.nextInt(11) * 25)
          val blue = "%03d".format(r.nextInt(11) *25)
          paintCharacter(g, c , i, j,Color.BLACK, red+green+blue)
        }
      }
      */
      //draw ui 
      for( j <- 1 to 40 ){
        paintCharacter(g, 249, 70, j, Color.BLACK, "255255255")
      }
      for( j <- 1 to 70 ){
        paintCharacter(g, 249, j, 40, Color.BLACK, "255255255")
      }

      slime.draw(g, width, height, matrix_width, matrix_height, tileset_handler) 
    }else{
      println("Couldn't load tileset, exiting.\n")
      this.main_frame.dispose()
    }
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
        slime.setY(slime.getY()-1)
        this.repaint()
      }else if(k == Key.Down){
        slime.setY(slime.getY()+1)
        this.repaint()
      }else if(k == Key.Left){
        slime.setX(slime.getX()-1)
        this.repaint()
      }else if(k == Key.Right){
        slime.setX(slime.getX()+1)
        this.repaint()
      }else{
        println(k+"  "+ k + "\n")
      }
    case MouseClicked(_,_,_,_,_) => 
      this.repaint()
      //println("map size : " + tileset_handler.getMapSize)
  }
}
