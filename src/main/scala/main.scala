import swing._
import swing.event._
import java.awt.{Color,Graphics2D,BasicStroke, Graphics}
import java.awt.geom._
import java.awt.image.BufferedImage                                           
import java.io.File                                                           
import javax.imageio.ImageIO


object ColorChanger {

  def copyImage(source: BufferedImage): BufferedImage = {
    val b:BufferedImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
    val g:Graphics = b.getGraphics();
    g.drawImage(source, 0, 0, null);
    g.dispose();
    return b;
  }


  def changeColor(original: BufferedImage, oldRed: Int, oldGreen: Int , oldBlue: Int, newRed: Int, newGreen: Int , newBlue: Int): BufferedImage = {
    val RGB_MASK:Int = 0x00FFFFFF
    val ALPHA_MASK:Int = 0xFF000000
    val oldRGB:Int = oldRed << 16 | oldGreen << 8 | oldBlue
    val toggleRGB = oldRGB ^ (newRed << 16 | newGreen << 8 | newBlue)
    val w:Int = original.getWidth()
    val h:Int = original.getHeight()

    val newImage: BufferedImage = copyImage(original)

    var rgb = newImage.getRGB(0,0,w,h,null,0,w)
    for( i <- 0 to (rgb.length-1)){
      if( (rgb(i) & RGB_MASK) == oldRGB){
        rgb(i) ^= toggleRGB
      }
    }
    
    newImage.setRGB(0,0,w,h,rgb,0,w)

    return newImage
  }
}



class GamePanel(main_frame: MainFrame ) extends Panel {
  var width = 1280
  var height = 720

  val matrix_width = 160
  val matrix_height = 90

  var px = 50
  var py = 40

  this.preferredSize = new Dimension(width, height)

  private var tilesetWhite:BufferedImage = null 
  tilesetWhite = ImageIO.read(new File("src/main/resources/2.png"))
  val tileSets = scala.collection.mutable.Map[String,BufferedImage]()
  tileSets("255255255") = tilesetWhite
  def getColoredTileset(color:String):BufferedImage = {
    val red = (color.substring(0,3)).toInt
    val green = (color.substring(3,6)).toInt
    val blue = (color.substring(6,9)).toInt
    println(red + "  " + green + "  " + blue + "\n")
    if( tileSets.contains(color) ){
      return tileSets(color)
    }else{
      tileSets(color) = ColorChanger.changeColor(tilesetWhite, 255,255,255,red,green,blue)
      return tileSets(color)
    }
  }

  def paintCharacter(g: Graphics2D,c: Int, px: Int, py: Int, bg: Color, fg: String){
    val x = c % 16 
    val y = c / 16 
    val dx1 = x*12 
    val dy1 = y*12
    val dx2 = dx1+11
    val dy2 = dy1+11
    val sx1 = px
    val sy1 = py
    val sx2 = px + 11
    val sy2 = py + 11
    g.drawImage(getColoredTileset(fg), sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2, bg , null)
  }
  
  override def paintComponent(g : Graphics2D) {
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
    
    g.setColor(Color.BLACK);
    g.fillRect(0,0, width, height)

    if (null != tilesetWhite)
      //g.setColor(Color.BLUE);
      //g.fillRect(px,py, 192, 192)
    paintCharacter(g, 1, px, py, Color.BLACK, "222093013")
      //g.drawImage(tilesetRed, px, py, null)
  }

  focusable = true
  listenTo(mouse.clicks)
  listenTo(keys)
  //**************************************************************//
  //************* reactions correspond à la MainLoop *************//
  //**************************************************************//
  this.reactions += {
    case KeyPressed(_,k,_,_) => println(k+"\n")
    case MouseClicked(_,_,_,_,_) => {}
  }
}

object My_app extends SimpleSwingApplication 
{
  def top = new MainFrame { main_frame =>
    title = "Enter the crapgeon!"
    resizable = false
    visible = true
    contents = new GamePanel(this)
  }
}
