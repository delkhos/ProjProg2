package rogue

import java.awt.{Color,Graphics2D, Graphics}

class Monster(x: Int, y: Int, sprite: Sprite, collidable: Boolean, maxHealth: Int, ia: ArtificialIntelligence, hitChance: Int, hitDamage: Int,name_arg: String,name_color: String) extends LivingEntity(x,y,sprite, collidable, maxHealth, hitChance, hitDamage, name_arg, name_color) {
  val own_ia = ia
  def processDecision(game: GameObject){
    own_ia.processDecision(game,this)
  }
}


class Goblin(x: Int, y: Int) extends Monster(x,y,

    new Sprite( Array[SubSprite](new SubSprite('g'.toInt,"000255000")), new Color((1.0).toFloat,(1.0).toFloat,(1.0).toFloat,(0.0).toFloat) )
    ,
    true,
    5,
    new GoblinIA
    ,40
    ,3
    ,"Goblin"
    ,"000255000"){
}
