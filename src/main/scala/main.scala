package rogue

import swing._

object My_app extends SimpleSwingApplication 
{
  def top = new MainFrame { main_frame =>
    title = "Enter the crapgeon!"
    resizable = false
    visible = true
    contents = new GamePanel(this)
  }
}
