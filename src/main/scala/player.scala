package rogue
//definition of the player, its inventory and its ability to wait one turn
class Player(pos: Position, sprite: Sprite, collidable: Boolean, maxHealth: Int, hitChance: Int, hitDamage: Int,name_arg: String,name_color: String) extends LivingEntity(pos,sprite,collidable,maxHealth,hitChance, hitDamage, name_arg, name_color) {
  val inventory = new Inventory(9, this)
  def waitAction(){
    Log.addLogMessage( new LogMessage( List(
      name , new SubMessage(" waited ", "255255255"))))
  }
}
