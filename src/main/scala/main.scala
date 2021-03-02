package rogue

import swing._

// This is the starting point of the swing application
object My_app extends SimpleSwingApplication 
{
<<<<<<< HEAD
  def top = new MainFrame { main_frame => //open a new window and start the game
=======
  def top = new MainFrame { main_frame =>
>>>>>>> e423417a8da2f9c1785f9af0e1de339bdf4d4bfa
    title = "Slime adventure!"
    resizable = false
    visible = true
    contents = new GamePanel(this)
  }
}
