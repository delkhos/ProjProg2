
package rogue 

class MainLoopVars(game_matrix_width:Int, game_matrix_height:Int, size: Int) {
  var dx = 0.0
  var dy = 0.0
  var sx = 0.0
  var sy = 0.0
  var posx_center = game_matrix_width/2.0
  var posy_center = game_matrix_height/2.0
  var dragging = false
  var current_size = size

  def getDX():Double={
    return dx
  }
  def getDY():Double={
    return dy
  }
  def getSX():Double={
    return sx
  }
  def getSY():Double={
    return sy
  }
  def getPOSX_CENTER():Double={
    return posx_center
  }
  def getPOSY_CENTER():Double={
    return posy_center
  }
  def getDRAGGING():Boolean={
    return dragging
  }
  def getCURRENT_SIZE():Int={
    return current_size
  }
  def setDX(new_dx: Double){
    dx = new_dx
  }
  def setDY(new_dy: Double){
    dy = new_dy
  }
  def setSX(new_sx: Double){
    sx = new_sx
  }
  def setSY(new_sy: Double){
    sy = new_sy
  }
  def setPOSX_CENTER(new_x: Double){
    posx_center = new_x
  }
  def setPOSY_CENTER(new_y: Double){
    posy_center = new_y
  }
  def setDRAGGING(new_dragging: Boolean){
    dragging = new_dragging
  }
  def setCURRENT_SIZE(new_size: Int){
    current_size = new_size
  }

}
