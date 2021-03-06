package rogue

import java.awt.{Color,Graphics2D, Graphics}

/*
 * An item is an entity that can be both on the ground and in the player's inventory
 * It has a pickup method to get inside the inventory.
 * It also has a "use" method to define what happens when it is used
 */
abstract class Item (arg_pos: Position, sprite: Sprite, arg_equipable: Boolean, name_arg: String, name_color: String) extends Entity(arg_pos,sprite) {
  var equipable = arg_equipable 
  var on_the_ground = true
  var pos_in_inventory = 0
  val name = new SubMessage(name_arg,name_color)
  def pickUp(game: GameObject ){
    if( game.player.pos == pos ){
      game.player.inventory.addItem(this)
    }
  }
  def use(game: GameObject){
  }
}

class HealingGoo (pos: Position) extends Item (
  pos,
  new Sprite( Array[SubSprite](new SubSprite(297,"000120000")),new Color(0.0f,0.0f,0.0f,0.0f)),
  false,
  "healing goo",
  "220030000"){

  override def use(game: GameObject){
    // heals 5 health points
    game.player.health += 5
    if(game.player.health > game.player.max_health)
      game.player.health = game.player.max_health
    // Removes itself from the inventory
    game.player.inventory.contents(pos_in_inventory) = null
  }

}

class UrchinStrike (pos:Position) extends Item (
  pos,
  new Sprite( Array[SubSprite](new SubSprite(15,"240060060")),new Color(0.0f,0.0f,0.0f,0.0f)),
  false,
  "urchin",
  "220030000"){
    override def use(game: GameObject){
      game.player.status = new UrchinStance(new Sprite( Array[SubSprite](new SubSprite(15,"240060060")), new Color(0.0f,0.0f,0.0f,0.0f)),3) :: game.player.status
      game.player.inventory.contents(pos_in_inventory) = null


    }

  }


class Trophy (pos: Position) extends Item (
  pos,
  new Sprite( Array[SubSprite]( new SubSprite(294,"255255000"), new SubSprite(295, "153051000") ),new Color(0.0f,0.0f,0.0f,0.0f)),
  false,
  "trophy",
  "255255000"){
    override def use(game: GameObject){
      game.trophy=true
    }
  }
