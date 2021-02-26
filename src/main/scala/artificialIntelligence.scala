package rogue

import scala.math.abs

abstract class ArtificialIntelligence(){
  def processDecision(game: GameObject, monster: Monster){
  }
}

object State{
  val Idle = 0
  val Chasing = 1
  val Attacking = 2
}

class GoblinIA extends ArtificialIntelligence(){
  var state = State.Idle
  var lastPlayerSeenPosition: Position = null
  override def processDecision(game: GameObject , goblin: Monster){
    println(goblin + " position = " + goblin.rx + " " + goblin.ry)
    if(game.lineOfSight(goblin.rx,goblin.ry,game.player.rx,game.player.ry)){
      if(abs(goblin.rx-game.player.rx) <= 1 && abs(goblin.ry-game.player.ry)<=1){ 
        state = State.Attacking
      }else{
        lastPlayerSeenPosition = new Position(game.player.rx, game.player.ry)
        if(state == State.Idle){
          Log.addLogMessage( new LogMessage( List(
            goblin.name , new SubMessage(" spotted ", "255255255")
              , game.player.name )
            )
          )
        }
        state = State.Chasing
      }
    }else if(lastPlayerSeenPosition != null && goblin.rx==lastPlayerSeenPosition.x && goblin.ry == lastPlayerSeenPosition.y){
      state = State.Idle
    }
    if(state == State.Idle){
      var nextPositions = List((1,1),(1,0),(1,-1),(-1,0),(-1,1),(-1,-1),(0,1),(0,-1))
      val r = scala.util.Random
      var i = r.nextInt(nextPositions.length)
      var delta = nextPositions(i)
      while( game.occupied(goblin.getX()+delta._1, goblin.getY()+delta._2) )
      {
        nextPositions = nextPositions.take(i) ++ nextPositions.drop(i + 1)
        i = r.nextInt(nextPositions.length)
        delta = nextPositions(i)
      }
      // vérifier le cas où il ne peut pas bouger du tout 
      if(nextPositions.length == 0){
        //println(goblin+"cannot move")
      
      }else{
        goblin.setX(goblin.getX + delta._1)
        goblin.setY(goblin.getY + delta._2)
      }
    }else if(state==State.Chasing){
      var path = game.a_star_path(goblin.rx,goblin.ry, lastPlayerSeenPosition.x,lastPlayerSeenPosition.y)
      goblin.rx = path(0).x
      goblin.ry = path(0).y
    }else if(state==State.Attacking){
      //println("attacking not yet implemented")
      goblin.attack(game.player)
    }
  }
}
