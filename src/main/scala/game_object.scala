package rogue 

import java.awt.{Color,Graphics2D, Graphics}

class GameObject(width: Int, height: Int) { 
  var first_floor = new MapAutomata(width, height)
  var player = new Environment(0, 0, new Sprite( Array[SubSprite](new SubSprite(234,"153051153")) , Color.BLACK), true)

  placePlayer()

  def placePlayer(){
    val r = scala.util.Random
    var x = r.nextInt(width)
    var y = r.nextInt(height)
    while((first_floor.getFloor())(x)(y) != 0){
      x = r.nextInt(width)
      y = r.nextInt(height)
    }
    player.setX(x)
    player.setY(y)
  }

  def newMap(){
    first_floor = new MapAutomata(width, height)
    placePlayer()
  }

  def getMap():MapAutomata = {
    return first_floor
  }
  def getPlayer():Entity = {
    return player
  }
}
