package rogue 

import java.awt.{Color,Graphics2D, Graphics}

class GameObject(width: Int, height: Int) { 
  var first_floor = new MapAutomata(width, height)
  var player = new Player(0, 0, new Sprite( Array[SubSprite](new SubSprite(178,"153051153")) , Color.BLACK), true, 10)

  placePlayer()

  def placePlayer(){
    val r = scala.util.Random
    var x = r.nextInt(width)
    var y = r.nextInt(height)
    while((first_floor.getFloor())(x)(y).getBlocking() == true){
      x = r.nextInt(width)
      y = r.nextInt(height)
    }
    player.setX(x)
    player.setY(y)
  }

  def lineOfSight(x1: Int,y1: Int,x2: Int, y2: Int ): Boolean = {
    val deltaX = scala.math.abs(x1-x2)
    val deltaY = scala.math.abs(y1-y2)
    val signX = if((x2-x1)<0) -1 else 1
    val signY = if((y2-y1)<0) -1 else 1
    var x = x1
    var y = y1
    if(deltaX > deltaY){
      var t = deltaY*2-deltaX
      do{
        if(t>=0){
          y += signY
          t -= deltaX*2
        }
        x += signX
        t += deltaY*2
        if(x==x2 && y==y2){
          return true
        }
      }while(first_floor.getFloor()(x)(y).getBlocking()==false)
      return false
    }else{
      var t = deltaX*2-deltaY
      do{
        if(t>=0){
          x += signX
          t -= deltaY*2
        }
        y += signY
        t += deltaX*2
        if(x==x2 && y==y2){
          return true
        }
      }while(first_floor.getFloor()(x)(y).getBlocking()==false)
      return false
    }
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
