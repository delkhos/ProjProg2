package rogue

import java.awt.{Color,Graphics2D, Graphics}

// definition of the montsers, with their properties
class Monster(arg_pos: Position, sprite: Sprite, collidable: Boolean, maxHealth: Int, ia: ArtificialIntelligence, hitChance: Int, hitDamage: Int,name_arg: String,name_color: String) extends LivingEntity(arg_pos,sprite, collidable, maxHealth, hitChance, hitDamage, name_arg, name_color) {
  val own_ia = ia
  def processDecision(game: GameObject){
    own_ia.processDecision(game,this)
  }
}

// creation of some monsters

class Goblin(arg_pos: Position) extends Monster(arg_pos,
  new Sprite( Array[SubSprite](new SubSprite(288,"161200060"), new SubSprite(287, "102051000")), new Color((1.0).toFloat,(1.0).toFloat,(1.0).toFloat,(0.0).toFloat) ),
    true,20,new IdleChaseIA,75,3,"Goblin","000255000"){}

class Hunter(arg_pos: Position) extends Monster(arg_pos,
  new Sprite( Array[SubSprite](new SubSprite(289,"072096112"),new SubSprite(290,"255000000")), new Color((1.0).toFloat,(1.0).toFloat,(1.0).toFloat,(0.0).toFloat) ),
  true,20,new HunterIA,85,5,"Hunter","080080080"){}

class Hoblin(arg_pos: Position) extends Monster(arg_pos,
  new Sprite( Array[SubSprite](new SubSprite(291,"255051000"), new SubSprite(292, "102051000")), new Color((1.0).toFloat,(1.0).toFloat,(1.0).toFloat,(0.0).toFloat) ),
  true,30,new SlowIA,30,7,"Hoblin","255051000"){}

class Bat(arg_pos: Position) extends Monster(arg_pos,
  new Sprite( Array[SubSprite](new SubSprite(293,"153000204")), new Color((1.0).toFloat,(1.0).toFloat,(1.0).toFloat,(0.0).toFloat) ),
  true,10,new FastIA,90,2,"Bat","153000204"){}


