import swing._
import swing.event._

object My_app extends SimpleSwingApplication 
{
  var width = 1280
  var height = 720

  def top = new MainFrame { main_frame =>
    title = "Launcher"
    resizable = false
    /********* Launcher ***************************************/
    /**********************************************************/
    val launch_button = new Button {
      text = "Launch game"
    }
    val launch_label = new Label {
      text = "Please choose your resolution's width"
    }
    val launch_width_field = new TextField{
      text = "1280"
    }
    val launch_width_label = new Label{
      text = "width = "
    }
    val launch_height_label = new Label {
      text = "height = 720"
    }
    val launcher_box = new BoxPanel(Orientation.Vertical) {
      launcher_box => 
      contents += launch_label
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += launch_width_label
        contents += launch_width_field
      }
      contents += launch_height_label
      contents += launch_button
      border = Swing.EmptyBorder(60, 80, 60, 60)
      listenTo(launch_button, launch_width_field)
      reactions += {
        case EditDone(`launch_width_field`) =>
          launch_width_field.text match {
            case "" => launch_height_label.text = "height = "
            case _ => 
              val f = (launch_width_field.text.toInt)/16*9
              launch_height_label.text = "height = " + f.toString
          }
        case ButtonClicked(launch_button) =>
          launch_width_field.text match {
            case "" => {}
            case _ => 
              val game_width = launch_width_field.text.toInt
              val game_height = game_width/16*9
              game_box.preferredSize = new Dimension(game_width, game_height)
              game_box.minimumSize = new Dimension(game_width, game_height)
              game_box.maximumSize = new Dimension(game_width, game_height)
              main_frame.contents = game_box
              main_frame.title = "Enter the crapgeon!"
          }
      }
    }
    contents = launcher_box
    /********* MainGame ***************************************/
    /**********************************************************/
    val game_box = new Panel {
      background = java.awt.Color.black

      override def paintComponent(g: java.awt.Graphics2D) {
        super.paintComponent(g);
        g.setColor(java.awt.Color.blue)
        if (mouseclicked) {
          g.fillOval(mouseX - 10, mouseY - 10, 20, 20)
          mouseclicked = false
        }
      }
    }
  }
}
