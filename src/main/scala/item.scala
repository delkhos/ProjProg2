package rogue

import java.awt.{Color,Graphics2D, Graphics}

abstract class Item (arg_pos: Position, sprite: Sprite, arg_equipable: Boolean, name_arg: String, name_color: String) extends Entity(arg_pos,sprite) {
  var equipable = arg_equipable 
  var on_the_ground = true
  var pos_in_inventory = 0
  val name = new SubMessage(name_arg,name_color)
  def pickUp(game: GameObject ){
    //println("posx = " + pos.x + " posy = "+pos.y)
    //println("player.posx = " + game.player.pos.x + " player.posy = "+game.player.pos.y)
    if( game.player.pos == pos ){
      game.player.inventory.addItem(this)
    }
  }
  def use(game: GameObject){
  }
}

class HealingGoo (pos: Position) extends Item (
  pos,
  new Sprite( Array[SubSprite](new SubSprite(15,"240060060")),new Color(0.0f,0.0f,0.0f,0.0f)),
  false,
  "healing goo",
  "220030000"){

  override def use(game: GameObject){
    game.player.health += 5
    game.player.inventory.contents(pos_in_inventory) = null
  }
}

