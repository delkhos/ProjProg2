package rogue

class Player(x: Int, y: Int, sprite: Sprite, collidable: Boolean, maxHealth: Int, hitChance: Int, hitDamage: Int,name_arg: String,name_color: String) extends LivingEntity(x,y,sprite,collidable,maxHealth,hitChance, hitDamage, name_arg, name_color) {
  val inventory = new Inventory(9, this)
}
