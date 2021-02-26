package rogue

import java.awt.Color
import java.awt.Graphics2D

abstract class Entity (x: Int, y: Int, sprite: Sprite) {
  var rx = x
  var ry = y
  def getX(): Int = {
    return rx
  }
  def getY(): Int = {
    return ry
  }
  def setX(nx: Int){
    rx=nx
  }
  def setY(ny: Int){
    ry=ny 
  }
  def draw(g: Graphics2D, current_size: Int, tileset_handler: TileSetHandler, dx: Int, dy: Int){
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
  def draw(g: Graphics2D, current_size: Int, tileset_handler: TileSetHandler, dx: Int, dy: Int, ratio: Double){
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


abstract class LivingEntity(x: Int, y: Int, sprite: Sprite, collidable: Boolean, maxHealth_arg: Int, hitChance: Int, hitDamage: Int, name_arg: String, name_color: String) extends Entity(x,y,sprite) {
  var health = maxHealth_arg
  val max_health = maxHealth_arg
  val name = new SubMessage(name_arg,name_color)
  def attack( attackee: LivingEntity){ 
    val r = scala.util.Random
    val attack_try = r.nextInt(100)
    if(attack_try <= hitChance){
      attackee.health -= hitDamage
      Log.addLogMessage( new LogMessage( List(
        name , new SubMessage(" attacked ", "255255255")
          , attackee.name , new SubMessage(" for ", "255255255")
          , new SubMessage( hitDamage.toString, "255000000")
          , new SubMessage( " damage(s)", "255255255")
         )
        )
      )
    }else{
      Log.addLogMessage( new LogMessage( List(
        name , new SubMessage(" attacked ", "255255255")
          , attackee.name , new SubMessage(" and missed", "255255255")
         )
        )
      )
    }
  }
}
