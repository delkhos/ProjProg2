package rogue 

class Position(arg_x:Int , arg_y: Int){
  var x = arg_x
  var y = arg_y
  def canEqual(a: Any) = a.isInstanceOf[Position]

  override def equals(that: Any): Boolean =
    that match {
      case that: Position => that.x == x && that.y == y
      case _ => false
  }

}
class PositionPath(arg_x:Int , arg_y: Int, arg_parent: PositionPath, arg_gcost: Int, arg_hcost: Int) extends Position(arg_x, arg_y){
  var gcost = arg_gcost
  var hcost = arg_hcost
  var parent = arg_parent

}
