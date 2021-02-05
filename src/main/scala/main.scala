import swing._
import swing.event._
import java.awt.{Color,Graphics2D,BasicStroke, Graphics}
import java.awt.geom._
import java.awt.image.BufferedImage                                           
import java.io.File                                                           
import javax.imageio.ImageIO
import java.awt.GraphicsEnvironment


object ColorChanger {

  val GFX_CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()

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
  def toCompatibleImage(image: BufferedImage): BufferedImage =  {
    /*
     * if image is already compatible and optimized for current system settings, simply return it
     */
    if (image.getColorModel().equals(GFX_CONFIG.getColorModel())) {
        return image;
    }

    // image is not optimized, so create a new image that is
    val new_image: BufferedImage = GFX_CONFIG.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());

    // get the graphics context of the new image to draw the old image on
    val g2d = new_image.getGraphics();

    // actually draw the image and dispose of context no longer needed
    g2d.drawImage(image, 0, 0, null);
    g2d.dispose();

    // return the new optimized image
    return new_image;
}
}

class GamePanel(main_frame: MainFrame ) extends Panel {
  var width = 1280
  var height = 720

  val matrix_width = 80
  val matrix_height = 45

  var px = 1
  var py = 1

  var tileset_size = 16
  var game_colors = Array[String]("255255255","255000000","000255000","000000255")
  var current_color = 0

  this.preferredSize = new Dimension(width, height)

  // GENRATING OUR TILESETS
  private var tilesetWhite:BufferedImage = null 
  tilesetWhite = ImageIO.read(new File("src/main/resources/3_16.png"))
  tilesetWhite = ColorChanger.toCompatibleImage(tilesetWhite)
  val tileSets = scala.collection.mutable.Map[String,BufferedImage]()
  tileSets("255255255") = tilesetWhite
  for( i <- 1 to  (game_colors.size-1)){
    getColoredTileset(game_colors(i)) 
  }
  // DONE

  def toInt(s: String): Int = {
    try {
      s.toInt
    } catch {
      case e: Exception => 255
    }
  }
  def getColoredTileset(color:String):BufferedImage = {
    val red = toInt(color.substring(0,3))
    val green = toInt(color.substring(3,6))
    val blue = toInt(color.substring(6,9))
    //println("Debug : " + red + "  " + green + "  " + blue + "\n")
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
    val dx1 = x*tileset_size 
    val dy1 = y*tileset_size
    val dx2 = dx1+(tileset_size-1)
    val dy2 = dy1+(tileset_size-1)
    val size = width/matrix_width
    val sx1 = (px-1)*size
    val sy1 = (py-1)*size
    val sx2 = sx1 + (size-1)
    val sy2 = sy1 + (size-1)
    g.drawImage(getColoredTileset(fg), sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2, bg , null)
  }
  
  override def paintComponent(g : Graphics2D) {
    /*
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)
    g.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED)
    */
    g.setColor(Color.BLACK);
    g.fillRect(0,0, width, height)

    // va falloir trouver un moyen de ne repaint que ce qui nous intéresse

    if (null != tilesetWhite){
      for(i <- 1 to 80 ){
        for(j <- 1 to 45 ){
          paintCharacter(g, (i*17+j*31)%256, i, j, Color.BLACK, game_colors(current_color))
        }
      }
    }
  }

  focusable = true
  listenTo(mouse.clicks)
  listenTo(keys)
  //**************************************************************//
  //************* reactions correspond à la MainLoop *************//
  //**************************************************************//
  this.reactions += {
    case KeyPressed(_,k,_,_) => println(k+"\n")
    case MouseClicked(_,_,_,_,_) => 
      current_color = (current_color+1)%4
      this.repaint()
      println("map size : " + tileSets.size)
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
