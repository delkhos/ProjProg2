package rogue 

/*
 * This class contains the relevant information that the mainLoop needs
 * to operate correctly
 */
class MainLoopVars(game_matrix_dim: Dimension, size: Int) {
  var dpos = new DPosition(0.0f,0.0f)
  var spos = new DPosition(0.0f,0.0f)
  var center_pos = new DPosition(0.0f, 0.0f)
  var dragging = false
  var current_size = size
}
