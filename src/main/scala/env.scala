package rogue

import java.awt.{Color,Graphics2D, Graphics}

class Environment(sprite: Sprite, blocking: Boolean) extends{
  def getBlocking(): Boolean = {
    return blocking
  }
  def draw(g: Graphics2D, current_size: Int, tileset_handler: TileSetHandler, dx: Int, dy: Int,rx: Int, ry: Int){
    g.setColor(sprite.getBgColor());
    val size:Float = current_size
    val sx1:Float = (rx)*size +dx //-rx
    val sy1:Float = (ry)*size + dy //-ry 
    val sx2:Float = sx1 + (size)
    val sy2:Float = sy1 + (size)
    g.fillRect(sx1.toInt,sy1.toInt, current_size, current_size)

    val elements = sprite.getElements()
    for(i <- 0 to (elements.size-1) ){
      val c = elements(i).getCharCode()
      val xv:Int = c % 16 
      val yv:Int = c / 16 
      val dx1:Int = xv*tileset_handler.getSize() 
      val dy1:Int = yv*tileset_handler.getSize()
      val dx2:Int = dx1+(tileset_handler.getSize())
      val dy2:Int = dy1+(tileset_handler.getSize())
      g.drawImage(tileset_handler.getColoredTileset(elements(i).getColor()), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)
    }
  }
  def draw(g: Graphics2D, current_size: Int, tileset_handler: TileSetHandler, dx: Int, dy: Int,rx: Int, ry:Int,  ratio: Double){
    val bg = sprite.getBgColor()
    g.setColor(new Color( (bg.getRed() * ratio).toInt , (bg.getGreen() * ratio).toInt, (bg.getBlue() * ratio).toInt    ));
    val size:Float = current_size
    val sx1:Float = (rx)*size +dx //-rx
    val sy1:Float = (ry)*size + dy //-ry 
    val sx2:Float = sx1 + (size)
    val sy2:Float = sy1 + (size)
    g.fillRect(sx1.toInt,sy1.toInt, current_size, current_size)

    val elements = sprite.getElements()
    for(i <- 0 to (elements.size-1) ){
      val fg = elements(i).getColor()
      val c = elements(i).getCharCode()
      val xv:Int = c % 16 
      val yv:Int = c / 16 
      val dx1:Int = xv*tileset_handler.getSize() 
      val dy1:Int = yv*tileset_handler.getSize()
      val dx2:Int = dx1+(tileset_handler.getSize())
      val dy2:Int = dy1+(tileset_handler.getSize())
      val red = "%03d".format((fg.substring(0,3).toInt*ratio).toInt)
      val green = "%03d".format((fg.substring(3,6).toInt*ratio).toInt)
      val blue = "%03d".format((fg.substring(6,9).toInt*ratio).toInt)
      g.drawImage(tileset_handler.getColoredTileset(red+green+blue), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)
    }
  } 
}


object Granite extends Environment(
  new Sprite(Array[SubSprite](new SubSprite(0,"255255255")) ,new Color(180,180,180))
  , true) {
}
object Empty extends Environment(
  new Sprite(Array[SubSprite](new SubSprite(250,"180180180")) ,Color.BLACK)
  , false) {
}
