package rogue

import scala.util.control.Breaks._

class Inventory(x: Int, owner: Player){
  val contents = Array.fill[Item](x)(null)
  val size = x

  def addItem(item: Item){
    for(i <- 0 to (size-1)){
      if(contents(i) == null){
        contents(i) = item
        item.on_the_ground = false
        item.pos_in_inventory = i
        Log.addLogMessage( new LogMessage( List(
          owner.name , new SubMessage(" picked up a ", "255255255")
            , item.name )
          )
        )
        return
      }
    }
    Log.addLogMessage( new LogMessage( List(
      owner.name , new SubMessage(" inventory is full", "255255255"))
      )
    )
  }

}
