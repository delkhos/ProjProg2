package rogue

import scala.util.control.Breaks._

class Inventory(x: Int, owner: Player){ //definition of the inventory that will be displayed on he side of the screen
  val contents = Array.fill[Item](x)(null)
  val size = x

  def addItem(item: Item){ // if an item is picked up, the inventory is updated, and the item disapear from the ground
    for(i <- 0 to (size-1)){
      if(contents(i) == null){ //looks for the first availabe slot in the inventory
        contents(i) = item //fills the slot
        item.on_the_ground = false //removes the item from the ground. If no slot is available, the item stays on the ground
        item.pos_in_inventory = i
        Log.addLogMessage( new LogMessage( List(
          owner.name , new SubMessage(" picked up a ", "255255255")
            , item.name )
          )
        )
        return // leave early if the inventory is was not full
      }
    }
    Log.addLogMessage( new LogMessage( List(
      owner.name , new SubMessage(" inventory is full", "255255255"))
      )
    )
  }

}
