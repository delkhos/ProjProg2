package rogue

import java.awt.{Color,Graphics2D, Graphics}

abstract class Item (x: Int, y: Int, sprite: Sprite, arg_equipable: Boolean, name_arg: String, name_color: String) extends Entity(x,y,sprite) {
  var equipable = arg_equipable 
  var on_the_ground = true
  var pos_in_inventory = 0
  val name = new SubMessage(name_arg,name_color)
  def pickUp(game: GameObject ){
    if( game.player.rx == rx && game.player.ry == ry ){
      game.player.inventory.addItem(this)
    }
  }
  def use(game: GameObject ){
  }
}

class HealingGoo (x: Int, y: Int) extends Item (
  x,y,
  new Sprite( Array[SubSprite](new SubSprite(15,"000255000")) , new Color(1.0f,1.0f,1.0f,0.0f)),
  false,
  "healing goo",
  "000255000"){
  }

