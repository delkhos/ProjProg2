package rogue

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
}

class Environment(x: Int, y: Int, sprite: Sprite, collidable: Boolean) extends Entity(x , y, sprite){
}

abstract class LivingEntity(x: Int, y: Int, sprite: Sprite, collidable: Boolean, maxHealth: Int) extends Entity(x,y,sprite) {
}

class Monster(x: Int, y: Int, sprite: Sprite, collidable: Boolean, maxHealth: Int, ia: ArtificialIntelligence) extends LivingEntity(x,y,sprite, collidable, maxHealth) {
}

class Player(x: Int, y: Int, sprite: Sprite, collidable: Boolean, maxHealth: Int, inventory: Inventory) extends LivingEntity(x,y,sprite,collidable,maxHealth) {
}

class Item (x: Int, y: Int, sprite: Sprite, equipable: Boolean, on_the_ground: Boolean) extends Entity(x,y,sprite) {
}
