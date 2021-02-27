
package rogue 

class Dimension(arg_width:Int , arg_height: Int){
  var width = arg_width
  var height = arg_height
  def canEqual(a: Any) = a.isInstanceOf[Dimension]

  override def equals(that: Any): Boolean =
    that match {
      case that: Dimension => that.width == width && that.height == height
      case _ => false
  }

}

object Dim0 extends Dimension(0,0)
{}

