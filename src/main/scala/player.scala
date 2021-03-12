package rogue

//definition of the player, its inventory and its ability to wait one turn
class Player(pos: Position, sprite: Sprite, collidable: Boolean, maxHealth: Int, hitChance: Int, hitDamage: Int,name_arg: String,name_color: String) extends LivingEntity(pos,sprite,collidable,maxHealth,hitChance, hitDamage, name_arg, name_color) {
  val inventory = new Inventory(9, this)
  var status: List[Status] = List()
  def waitAction(){
    Log.addLogMessage( new LogMessage( List(
      name , new SubMessage(" waited ", "255255255"))))
  }
}

abstract class Status(sprite: Sprite, argDuration: Int) {
  var duration: Int = argDuration
  def effect(game: GameObject){}
}

class UrchinStance(sprite: Sprite, argDuration: Int) extends Status(sprite, argDuration){
  override def effect(game:GameObject){
    game.monsters.foreach(
      m => if (game.player.pos.adjacent(m.pos)) {
        m.health-=5 
        Log.addLogMessage( new LogMessage( List(
          m.name , new SubMessage(" got spiked for 5 damages.", "255255255"))))
        if (m.health<=0){
          m.state = State.Dead
        }
    } 
    )
  }
}

class HealOverTime(sprite: Sprite, argDuration: Int) extends Status(sprite, argDuration){
  override def effect(game: GameObject){
    game.player.health+= 3
  }
}

