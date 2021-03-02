package rogue

import swing._

object My_app extends SimpleSwingApplication 
{
  def top = new MainFrame { main_frame => //open a new window and start the game
    title = "Slime adventure!"
    resizable = false
    visible = true
    contents = new GamePanel(this)
  }
}
