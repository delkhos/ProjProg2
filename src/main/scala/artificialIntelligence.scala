package rogue

import scala.math.abs

abstract class ArtificialIntelligence(){ // definition of the core elements to rule the behavior of the PNJs
  def processDecision(game: GameObject, monster: Monster){
  }
}

// This object serves the purpose of an enumeration
object State{ //definitions to make the code easier to understand
  val Dead = -1
  val Idle = 0
  val Chasing = 1
  val Attacking = 2
}
//a simple AI, if the monster sees you, it will try to follow you, until it can attack  
// if the monster can't see you anymore it will go to the last known position of the hero
class IdleChaseIA extends ArtificialIntelligence(){ 
  var lastPlayerSeenPosition: Position = null //it has not seen the player already
  override def processDecision(game: GameObject , monster: Monster){
    if(monster.state != State.Dead){ //test in case the monster is dead in this turn and still is in the memory
      //println(monster + " " + monster.pos.x + " " + monster.pos.y)
      if(game.lineOfSight(monster.pos,game.player.pos)){ //determines if the monster can see the player
        if(abs(monster.pos.x-game.player.pos.x) <= 1 && abs(monster.pos.y-game.player.pos.y)<=1){ //determine if the player is within the attack range
          monster.state = State.Attacking
        }else{
          lastPlayerSeenPosition = new Position(game.player.pos.x, game.player.pos.y) //updates the known player position if the monster can see it
          if(monster.state == State.Idle){
            // when the monster spots the player, prints a message in the log stating that event
            Log.addLogMessage( new LogMessage( List(
              monster.name , new SubMessage(" spotted ", "255255255")
                , game.player.name )
              )
            )
          }
          monster.state = State.Chasing
        }
        }else if(lastPlayerSeenPosition != null && monster.pos== lastPlayerSeenPosition){ //if the monster loses the track of the player, it will idle again
        monster.state = State.Idle
      }
      if(monster.state == State.Idle){ //random movement while idling
        var nextPositions = List((1,1),(1,0),(1,-1),(-1,0),(-1,1),(-1,-1),(0,1),(0,-1))
        val r = scala.util.Random
        var i = r.nextInt(nextPositions.length)
        var delta = nextPositions(i)
        while( game.occupied(monster.pos.translate(delta._1,delta._2)) ) //look for an unoccupied tile around the monster
        {
          nextPositions = nextPositions.take(i) ++ nextPositions.drop(i + 1)
          i = r.nextInt(nextPositions.length)
          delta = nextPositions(i)
        }
        if(nextPositions.length == 0){//if the monster can not move,it does nothing
      
        }else{
          monster.pos = monster.pos.translate(delta._1, delta._2) // else it moves
        }
      }else if(monster.state==State.Chasing){
        var path = game.a_star_path(monster.pos, lastPlayerSeenPosition) //follow the player, using an A* algorithm, see game_object file
        monster.pos = path(0)
      }else if(monster.state==State.Attacking){ //the monster attack the player and then turn into a non-attacking state, to avoid unwanted attack with turn-based mechanics
        monster.attack(game.player)
        monster.state=State.Chasing
      }
    }
  }
}

class HunterIA extends ArtificialIntelligence(){ //similar to IdleChaseIA, but the monster always know where the player is
  override def processDecision(game: GameObject , monster: Monster){
    if(monster.state != State.Dead){
      //println(monster + " " + monster.pos.x + " " + monster.pos.y)
      if(abs(monster.pos.x-game.player.pos.x) <= 1 && abs(monster.pos.y-game.player.pos.y)<=1){ 
        monster.state = State.Attacking
      }else{
        if(monster.state == State.Idle){ //at the first turn, the monster locate the player and stop idling
          Log.addLogMessage( new LogMessage( List(monster.name , new SubMessage(" is chasing ", "255255255"), game.player.name ) ) )
        }
        monster.state = State.Chasing
      }
        
        if(monster.state == State.Idle){ // even if it can't see the player
          monster.state == State.Chasing
          Log.addLogMessage( new LogMessage( List(monster.name , new SubMessage(" is chasing ", "255255255"), game.player.name ) ) )
          
        }else if(monster.state==State.Chasing){
          var path = game.a_star_path(monster.pos, game.player.pos)
          monster.pos = path(0)

    
        }else if(monster.state==State.Attacking){
          monster.attack(game.player)
          monster.state= State.Chasing
        }
    }
  }
}

class FastIA extends ArtificialIntelligence(){ //similar to the IdleChaseIA but it acts twice a turn
  var lastPlayerSeenPosition: Position = null
  def halfProcess(game: GameObject, monster: Monster){
    if(monster.state != State.Dead){
      //println(monster + " " + monster.pos.x + " " + monster.pos.y)
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
        if(nextPositions.length == 0){
      
        }else{
          monster.pos = monster.pos.translate(delta._1, delta._2)
        }
      }else if(monster.state==State.Chasing){
        var path = game.a_star_path(monster.pos, lastPlayerSeenPosition)
        monster.pos = path(0)
      }else if(monster.state==State.Attacking){
        monster.attack(game.player)
        monster.state=State.Chasing
      }
    }
  }
  override def processDecision(game: GameObject , monster: Monster){ // the idleChase Process is here a subfunction, applied twice
    halfProcess(game,monster)
    halfProcess(game,monster)
  } 
}

class SlowIA extends ArtificialIntelligence(){ // similar to the IdleChaseIA, but it acts only once every two turns
  var lastPlayerSeenPosition: Position = null
  var turnCount: Int = 0
  override def processDecision(game: GameObject , monster: Monster){
    turnCount += 1
    turnCount %= 2
    if(monster.state != State.Dead && turnCount !=0){
      //println(monster + " " + monster.pos.x + " " + monster.pos.y)
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
        if(nextPositions.length == 0){
      
        }else{
          monster.pos = monster.pos.translate(delta._1, delta._2)
        }
      }else if(monster.state==State.Chasing){
        var path = game.a_star_path(monster.pos, lastPlayerSeenPosition)
        monster.pos = path(0)
      }else if(monster.state==State.Attacking){
        monster.attack(game.player)
        monster.state= State.Chasing
      }
    }
  }
}


