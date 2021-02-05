package rogue

import java.awt.image.BufferedImage                                           
import java.io.File                                                           
import javax.imageio.ImageIO
import java.awt.GraphicsEnvironment
import java.awt.{Color,Graphics2D, Graphics}

class TileSetHandler(size: Int,path: String ) {

  val GFX_CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()

  private var tilesetWhite:BufferedImage = null 
  tilesetWhite = loadTileSet(path)
  tilesetWhite = toCompatibleImage(tilesetWhite)
  private val tileSets = scala.collection.mutable.Map[String,BufferedImage]()
  tileSets("255255255") = tilesetWhite

  def copyImage(source: BufferedImage): BufferedImage = {
    val b:BufferedImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
    val g:Graphics = b.getGraphics();
    g.drawImage(source, 0, 0, null);
    g.dispose();
    return b;
  }

  def loadTileSet(p: String): BufferedImage = {
    val img = ImageIO.read(new File(p))
    return img
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
  def toInt(s: String): Int = {
    try {
      s.toInt
    } catch {
      case e: Exception => 255
    }
  }

  def isReady():Boolean = {
    return tilesetWhite != null      
  }
  def getMapSize():Int = {
    return tileSets.size      
  }
  def getSize():Int = {
    return size      
  }
  def getColoredTileset(color:String):BufferedImage = {
    val red = toInt(color.substring(0,3))
    val green = toInt(color.substring(3,6))
    val blue = toInt(color.substring(6,9))
    //println("Debug : " + red + "  " + green + "  " + blue + "\n")
    if( tileSets.contains(color) ){
      return tileSets(color)
    }else{
      tileSets(color) = changeColor(tilesetWhite, 255,255,255,red,green,blue)
      return tileSets(color)
    }
  }
}
