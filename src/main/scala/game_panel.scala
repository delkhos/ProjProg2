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

  def paintCharacter(g: Graphics2D,c: Int, px: Int, py: Int, bg: Color, fg: String){
    val x = c % 16 
    val y = c / 16 
    val dx1 = x*tileset_handler.getSize() 
    val dy1 = y*tileset_handler.getSize()
    val dx2 = dx1+(tileset_handler.getSize()-1)
    val dy2 = dy1+(tileset_handler.getSize()-1)
    val size = width/matrix_width
    val sx1 = (px-1)*size
    val sy1 = (py-1)*size
    val sx2 = sx1 + (size-1)
    val sy2 = sy1 + (size-1)
    g.setColor(bg);
    g.fillRect(sx1,sy1, tileset_handler.getSize(), tileset_handler.getSize())
    g.drawImage(tileset_handler.getColoredTileset(fg), sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2, null)
  }
  
  override def paintComponent(g : Graphics2D) {
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)
    g.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED)

    g.setColor(Color.BLACK);
    g.fillRect(0,0, width, height)

    if (tileset_handler.isReady()){
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
        this.width += 1
        this.height = this.width*9/16
        this.preferredSize = new Dimension(this.width, this.height)
        this.main_frame.pack()
        this.repaint()
        println("on grandit")
      }else if(k == Key.M){
        this.width -= 1
        this.height = this.width*9/16
        this.preferredSize = new Dimension(this.width, this.height)
        this.main_frame.pack()
        this.repaint()
        println("on grandit")
      }else{
        println(k+"  "+ k + "\n")
      }
    case KeyTyped(_,k,_,_) => 
      this.repaint()
      println("map size : " + tileset_handler.getMapSize)
    case MouseClicked(_,_,_,_,_) => 
      this.repaint()
      println("map size : " + tileset_handler.getMapSize)
  }
}
