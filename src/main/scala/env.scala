package rogue

import java.awt.{Color,Graphics2D, Graphics}

case class PairBiome(element: Environment, proba: Int){
}

//a biome is a set of tile and color, used to define how to "paint" the environment
class Biome(elements: List[PairBiome], minsize_arg: Int, maxsize_arg: Int){ 
  val minsize = minsize_arg
  val maxsize = maxsize_arg
  val r = scala.util.Random
  def getElement(): Environment = {
    for(i: Int <- 0 to (elements.length-1)){
      if(r.nextInt(100)<= elements(i).proba){
        return elements(i).element
      }
    }
    return elements(elements.length-1).element
  }
}

class Environment(sprite: Sprite, blocking: Boolean, tall: Boolean){ //an environment is an element of the landscape: either the ground or a wall ...
  def getBlocking(): Boolean = {
    return blocking 
  }
  def getTall(): Boolean = {
    return tall 
  }

  //draws the element's sprite at a given position [pos] 
  def draw(g: Graphics2D, current_size: Int, tileset_handler: TileSetHandler, dpos: DPosition, pos: Position){
    g.setColor(sprite.getBgColor());
    val size:Float = current_size
    val sx1:Float = (pos.x)*size +dpos.x //-rx
    val sy1:Float = (pos.y)*size + dpos.y //-ry 
    val sx2:Float = sx1 + (size)
    val sy2:Float = sy1 + (size)
    g.fillRect(sx1.toInt,sy1.toInt, current_size, current_size)

    val elements = sprite.getElements()//draws the subsprites to form a sprite
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

  // Same as the function above, but overloaded to print it in a greyer shade
  def draw(g: Graphics2D, current_size: Int, tileset_handler: TileSetHandler, dpos: DPosition, pos: Position,  ratio: Double){
    val bg = sprite.getBgColor()
    g.setColor(new Color( (bg.getRed() * ratio).toInt , (bg.getGreen() * ratio).toInt, (bg.getBlue() * ratio).toInt    ));
    val size:Float = current_size
    val sx1:Float = (pos.x)*size +dpos.x //-rx
    val sy1:Float = (pos.y)*size + dpos.y //-ry 
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

object Neutral extends Biome (List(
  new PairBiome(  Empty, 100),
  ), 10 ,15)
  {
}

object Lake extends Biome (List(
  new PairBiome(  Lake1, 50),
  new PairBiome( Lake2, 100)
  ), 10 ,13)
  {
}
object Field extends Biome (List(
  new PairBiome(  Flower, 5),
  new PairBiome( HighHerb, 15),
  new PairBiome( Herb, 100)
  ), 16 ,23)
  {
}
object DirtField extends Biome (List(
  new PairBiome(  DirtPebble, 35),
  new PairBiome( Dirt, 100)
  ), 20 ,27)
  {
}

object Lake1 extends Environment( //definitions of the environment parts, here a wall
  new Sprite(Array[SubSprite](new SubSprite(247,"005036161")), new Color(30,69,227)) , false, false)  {
}
object Lake2 extends Environment( //definitions of the environment parts, here a wall
  new Sprite(Array[SubSprite](new SubSprite(247,"005036161")), new Color(46,88,255)) , false, false)  {
}
object HighHerb extends Environment( //definitions of the environment parts, here a wall
  new Sprite(Array[SubSprite](new SubSprite(244,"017214024")), new Color(0,0,0)) , false, true)  {
}
object Herb extends Environment( //definitions of the environment parts, here a wall
  new Sprite(Array[SubSprite](new SubSprite(231,"017214024")), new Color(0,0,0)) , false, false)  {
}
object Dirt extends Environment( //definitions of the environment parts, here a wall
  new Sprite(Array[SubSprite](new SubSprite(250,"000000000")), new Color(133,81,4)) , false, false)  {
}
object DirtPebble extends Environment( //definitions of the environment parts, here a wall
  new Sprite(Array[SubSprite](new SubSprite(249,"000000000")), new Color(133,81,4)) , false, false)  {
}
object Flower extends Environment( //definitions of the environment parts, here a wall
  new Sprite(Array[SubSprite](new SubSprite(173,"017214024"),
    new SubSprite(42,"255212059")
    ),
    new Color(0,0,0)) , false, false ) {
}

object Granite extends Environment( //definitions of the environment parts, here a wall
  new Sprite(Array[SubSprite](new SubSprite(0,"255255255")) ,new Color(180,180,180))
  , true, true) {
}

object Empty extends Environment( // and here, several floor types
  new Sprite(Array[SubSprite](new SubSprite(250,"180180180")) ,Color.BLACK)
  , false, false) {
}
