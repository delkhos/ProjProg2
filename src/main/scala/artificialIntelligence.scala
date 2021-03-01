package rogue

import scala.math.abs

abstract class ArtificialIntelligence(){
  def processDecision(game: GameObject, monster: Monster){
  }
}

object State{
  val Dead = -1
  val Idle = 0
  val Chasing = 1
  val Attacking = 2
}

class IdleChaseIA extends ArtificialIntelligence(){
  var lastPlayerSeenPosition: Position = null
  override def processDecision(game: GameObject , monster: Monster){
    if(monster.state != State.Dead){
      println(monster + " " + monster.pos.x + " " + monster.pos.y)
      if(game.lineOfSight(monster.pos,game.player.pos)){
        if(abs(monster.pos.x-game.player.pos.x) <= 1 && abs(monster.pos.y-game.player.pos.y)<=1){ 
          monster.state = State.Attacking
        }else{
          lastPlayerSeenPosition = new Position(game.player.pos.x, game.player.pos.y)
          if(monster.state == State.Idle){
            Log.addLogMessage( new LogMessage( List(
              monster.name , new SubMessage(" spotted ", "255255255")
                , game.player.name )
              )
            )
          }
          monster.state = State.Chasing
        }
        }else if(lastPlayerSeenPosition != null && monster.pos== lastPlayerSeenPosition){
        monster.state = State.Idle
      }
      if(monster.state == State.Idle){
        var nextPositions = List((1,1),(1,0),(1,-1),(-1,0),(-1,1),(-1,-1),(0,1),(0,-1))
        val r = scala.util.Random
        var i = r.nextInt(nextPositions.length)
        var delta = nextPositions(i)
        while( game.occupied(monster.pos.translate(delta._1,delta._2)) )
        {
          nextPositions = nextPositions.take(i) ++ nextPositions.drop(i + 1)
          i = r.nextInt(nextPositions.length)
          delta = nextPositions(i)
        }
        // vérifier le cas où il ne peut pas bouger du tout 
        if(nextPositions.length == 0){
          //println(monster+"cannot move")
      
        }else{
          monster.pos = monster.pos.translate(delta._1, delta._2)
        }
      }else if(monster.state==State.Chasing){
        var path = game.a_star_path(monster.pos, lastPlayerSeenPosition)
        monster.pos = path(0)
      }else if(monster.state==State.Attacking){
        //println("attacking not yet implemented")
        monster.attack(game.player)
        monster.state=State.Chasing
      }
    }
  }
}

class HunterIA extends ArtificialIntelligence(){
  override def processDecision(game: GameObject , monster: Monster){
    if(monster.state != State.Dead){
      println(monster + " " + monster.pos.x + " " + monster.pos.y)
      if(abs(monster.pos.x-game.player.pos.x) <= 1 && abs(monster.pos.y-game.player.pos.y)<=1){ 
        monster.state = State.Attacking
      }else{
        if(monster.state == State.Idle){
          Log.addLogMessage( new LogMessage( List(monster.name , new SubMessage(" is chasing ", "255255255"), game.player.name ) ) )
        }
        monster.state = State.Chasing
      }
        
        if(monster.state == State.Idle){
          monster.state == State.Chasing
          Log.addLogMessage( new LogMessage( List(monster.name , new SubMessage(" is chasing ", "255255255"), game.player.name ) ) )
          
        }else if(monster.state==State.Chasing){
          var path = game.a_star_path(monster.pos, game.player.pos)
          monster.pos = path(0)

    
        }else if(monster.state==State.Attacking){
        //println("attacking not yet implemented")
          monster.attack(game.player)
          monster.state= State.Chasing
        }
    }
  }
}

class FastIA extends ArtificialIntelligence(){
  var lastPlayerSeenPosition: Position = null
  def halfProcess(game: GameObject, monster: Monster){
    if(monster.state != State.Dead){
      println(monster + " " + monster.pos.x + " " + monster.pos.y)
      if(game.lineOfSight(monster.pos,game.player.pos)){
        if(abs(monster.pos.x-game.player.pos.x) <= 1 && abs(monster.pos.y-game.player.pos.y)<=1){ 
          monster.state = State.Attacking
        }else{
          lastPlayerSeenPosition = new Position(game.player.pos.x, game.player.pos.y)
          if(monster.state == State.Idle){
            Log.addLogMessage( new LogMessage( List(
              monster.name , new SubMessage(" spotted ", "255255255")
                , game.player.name )
              )
            )
          }
          monster.state = State.Chasing
        }
        }else if(lastPlayerSeenPosition != null && monster.pos== lastPlayerSeenPosition){
        monster.state = State.Idle
      }
      if(monster.state == State.Idle){
        var nextPositions = List((1,1),(1,0),(1,-1),(-1,0),(-1,1),(-1,-1),(0,1),(0,-1))
        val r = scala.util.Random
        var i = r.nextInt(nextPositions.length)
        var delta = nextPositions(i)
        while( game.occupied(monster.pos.translate(delta._1,delta._2)) )
        {
          nextPositions = nextPositions.take(i) ++ nextPositions.drop(i + 1)
          i = r.nextInt(nextPositions.length)
          delta = nextPositions(i)
        }
        // vérifier le cas où il ne peut pas bouger du tout 
        if(nextPositions.length == 0){
          //println(monster+"cannot move")
      
        }else{
          monster.pos = monster.pos.translate(delta._1, delta._2)
        }
      }else if(monster.state==State.Chasing){
        var path = game.a_star_path(monster.pos, lastPlayerSeenPosition)
        monster.pos = path(0)
      }else if(monster.state==State.Attacking){
        //println("attacking not yet implemented")
        monster.attack(game.player)
        monster.state=State.Chasing
      }
    }
  }
  override def processDecision(game: GameObject , monster: Monster){
    halfProcess(game,monster)
    halfProcess(game,monster)
  }
}

class SlowIA extends ArtificialIntelligence(){
  var lastPlayerSeenPosition: Position = null
  var turnCount: Int = 0
  override def processDecision(game: GameObject , monster: Monster){
    turnCount += 1
    turnCount %= 2
    if(monster.state != State.Dead && turnCount !=0){
      println(monster + " " + monster.pos.x + " " + monster.pos.y)
      if(game.lineOfSight(monster.pos,game.player.pos)){
        if(abs(monster.pos.x-game.player.pos.x) <= 1 && abs(monster.pos.y-game.player.pos.y)<=1){ 
          monster.state = State.Attacking
        }else{
          lastPlayerSeenPosition = new Position(game.player.pos.x, game.player.pos.y)
          if(monster.state == State.Idle){
            Log.addLogMessage( new LogMessage( List(
              monster.name , new SubMessage(" spotted ", "255255255")
                , game.player.name )
              )
            )
          }
          monster.state = State.Chasing
        }
        }else if(lastPlayerSeenPosition != null && monster.pos== lastPlayerSeenPosition){
        monster.state = State.Idle
      }
      if(monster.state == State.Idle){
        var nextPositions = List((1,1),(1,0),(1,-1),(-1,0),(-1,1),(-1,-1),(0,1),(0,-1))
        val r = scala.util.Random
        var i = r.nextInt(nextPositions.length)
        var delta = nextPositions(i)
        while( game.occupied(monster.pos.translate(delta._1,delta._2)) )
        {
          nextPositions = nextPositions.take(i) ++ nextPositions.drop(i + 1)
          i = r.nextInt(nextPositions.length)
          delta = nextPositions(i)
        }
        // vérifier le cas où il ne peut pas bouger du tout 
        if(nextPositions.length == 0){
          //println(monster+"cannot move")
      
        }else{
          monster.pos = monster.pos.translate(delta._1, delta._2)
        }
      }else if(monster.state==State.Chasing){
        var path = game.a_star_path(monster.pos, lastPlayerSeenPosition)
        monster.pos = path(0)
      }else if(monster.state==State.Attacking){
        //println("attacking not yet implemented")
        monster.attack(game.player)
        monster.state= State.Chasing
      }
    }
  }
}


