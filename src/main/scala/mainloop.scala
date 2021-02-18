package rogue 

import swing.event._
import swing._
import java.awt.event.KeyEvent

object MainLoopObject{
  def mainLoop(panel:GamePanel, vars:MainLoopVars, renderer: Renderer, game: GameObject, e: Event, game_matrix_width: Int, game_matrix_height: Int){
    e match {
      case KeyPressed(_,k,_,_) => 
        if(k == Key.Asterisk){
          vars.setCURRENT_SIZE(renderer.getTileSize())
          vars.setDX(0)
          vars.setDY(0)
          panel.repaint()
          println("Current resolution : size="+vars.getCURRENT_SIZE())
        }else if(k == Key.Up){
          if(game.first_floor.getFloor()(game.player.getX())(game.player.getY()-1) != 1 ){
            game.player.setY(game.player.getY()-1)
            panel.repaint()
          }
        }else if(k == Key.Down){
          if(game.first_floor.getFloor()(game.player.getX())(game.player.getY()+1) != 1 ){
            game.player.setY(game.player.getY()+1)
            panel.repaint()
          }
        }else if(k == Key.Left){
          if(game.first_floor.getFloor()(game.player.getX()-1)(game.player.getY()) != 1 ){
            game.player.setX(game.player.getX()-1)
            panel.repaint()
          }
        }else if(k == Key.Right){
          if(game.first_floor.getFloor()(game.player.getX()+1)(game.player.getY()) != 1 ){
            game.player.setX(game.player.getX()+1)
            panel.repaint()
          }
        }else{
          panel.repaint()
          println(k+"  "+ k + "\n")
        }
      case MousePressed(_,coord,_,_,_) => 
        //println("clicked")
        vars.setDRAGGING(true)
      case MouseClicked(_,coord,_,_,_) => 
        var clicked_x = ((coord.getX() -vars.getDX())/(vars.getCURRENT_SIZE().toFloat)).toInt
        var clicked_y = ((coord.getY() -vars.getDY())/(vars.getCURRENT_SIZE().toFloat)).toInt
        //println("j'ai cliqué en "+clicked_x+"  "+clicked_y)
        //println("perso en: "+game.player.getX()+"  "+game.player.getY())
        if( game.first_floor.getFloor()(clicked_x)(clicked_y) != 1 && scala.math.abs(clicked_x- game.player.getX())<=1 && scala.math.abs(clicked_y- game.player.getY())<=1){
          game.player.setX(clicked_x)
          game.player.setY(clicked_y)
          panel.repaint()
        }

      case MouseDragged(_,coord,_) => 
        val sx2 = coord.getX()
        val sy2 = coord.getY()
        //println("resolving DRAG")
        if(vars.getDRAGGING()){
          //println("first grab OK")
          vars.setSX(sx2)
          vars.setSY(sy2)
          vars.setDRAGGING(false)
        }
        vars.setDX(vars.getDX() + (sx2-vars.getSX())) 
        vars.setDY(vars.getDY() + (sy2-vars.getSY())) 
        //println("dragging dsx= "+(sx2-sx).toInt+"  "+(sy2-sy).toInt)
        vars.setSX(sx2)
        vars.setSY(sy2)
        vars.setPOSX_CENTER((renderer.getTileSize()*game_matrix_width/2 -vars.getDX())/(vars.getCURRENT_SIZE().toFloat))
        vars.setPOSY_CENTER((renderer.getTileSize()*game_matrix_height/2 -vars.getDY())/(vars.getCURRENT_SIZE().toFloat))
        panel.repaint()
      case MouseReleased(_,coord,_,_,_) => 
        vars.setDRAGGING(false)
        //println("La position centrale= "+((renderer.getTileSize()*game_matrix_width/2 -dx)/(current_size.toFloat))+" "+((renderer.getTileSize()*game_matrix_height/2 - dy )/(current_size.toFloat)))
      case MouseWheelMoved(_,coord,_,n) =>
        val old = vars.getCURRENT_SIZE()
        vars.setCURRENT_SIZE(old+n)
        if(vars.getCURRENT_SIZE()<renderer.getTileSize()){
          vars.setCURRENT_SIZE(renderer.getTileSize())
        }else{
          vars.setDX(vars.getDX() - n*vars.getPOSX_CENTER())
          vars.setDY(vars.getDY() - n*vars.getPOSY_CENTER())
        }
        panel.repaint()
        //println("Current resolution : size="+current_size)
      case _ => {}
    }
  }
}
