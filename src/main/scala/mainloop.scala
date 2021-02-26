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
          if(game.occupied(game.player.getX(),game.player.getY()-1)==false ){
            game.player.setY(game.player.getY()-1)
            game.processDecisions()
            panel.repaint()
          }else{ 
            var monsteropt = game.monsters.find(m => (m.rx==game.player.getX()-1 && m.ry == game.player.getY()))
            monsteropt match{
              case Some(targetMonster) => {
                game.player.attack(targetMonster)
                if (targetMonster.health<0) {
                  targetMonster.own_ia.state = State.Dead
                  Log.addLogMessage( new LogMessage( List( targetMonster.name , new SubMessage(" died.", "255255255"))))
                }
                game.processDecisions()
                panel.repaint()
              
              }
              case None => {}
            }
          }
        }else if(k == Key.Down){
          if(game.occupied(game.player.getX(),game.player.getY()+1)==false ){
            game.player.setY(game.player.getY()+1)
            game.processDecisions()
            panel.repaint()
          }else{ 
            var monsteropt = game.monsters.find(m => (m.rx==game.player.getX() && m.ry == game.player.getY()+1))
            monsteropt match{
              case Some(targetMonster) => {
                game.player.attack(targetMonster)
                if (targetMonster.health<0) {
                  targetMonster.own_ia.state = State.Dead
                  Log.addLogMessage( new LogMessage( List( targetMonster.name , new SubMessage(" died.", "255255255"))))
                }
                game.processDecisions()
                panel.repaint()
              
              }
              case None => {}
            }
          }
        }else if(k == Key.Left){
          if(game.occupied(game.player.getX()-1,game.player.getY())==false ){
            game.player.setX(game.player.getX()-1)
            game.processDecisions()
            panel.repaint()
          }else{ 
            var monsteropt = game.monsters.find(m => (m.rx==game.player.getX()-1 && m.ry == game.player.getY()))
            monsteropt match{
              case Some(targetMonster) => {
                game.player.attack(targetMonster)
                if (targetMonster.health<0) {
                  targetMonster.own_ia.state = State.Dead
                  Log.addLogMessage( new LogMessage( List( targetMonster.name , new SubMessage(" died.", "255255255"))))
                }
                game.processDecisions()
                panel.repaint()
              
              }
              case None => {}
            }
          }

        }else if(k == Key.Right){
          if(game.occupied(game.player.getX()+1,game.player.getY())==false ){
            game.player.setX(game.player.getX()+1)
            game.processDecisions()
            panel.repaint()
          }
          else{ 
            var monsteropt = game.monsters.find(m => (m.rx==game.player.getX()+1 && m.ry == game.player.getY()))
            monsteropt match{
              case Some(targetMonster) => {
                game.player.attack(targetMonster)
                if (targetMonster.health<0) {
                  targetMonster.own_ia.state = State.Dead
                  Log.addLogMessage( new LogMessage( List( targetMonster.name , new SubMessage(" died.", "255255255"))))
                }
                game.processDecisions()
                panel.repaint()
              }
              case None => {}
            }
          }
        }else{
          //panel.repaint()
          println(k+"  "+ k + "\n")
        }
      case MousePressed(_,coord,_,_,_) => 
        //println("clicked")
        vars.setDRAGGING(true)
      case MouseMoved(_,coord,_) => 
        var clicked_x = ((coord.getX() -vars.getDX())/(vars.getCURRENT_SIZE().toFloat)).toInt
        var clicked_y = ((coord.getY() -vars.getDY())/(vars.getCURRENT_SIZE().toFloat)).toInt
        //println("j'ai cliqué en "+clicked_x+"  "+clicked_y)
        //println("perso en: "+game.player.getX()+"  "+game.player.getY())
        //println("angle : "+angle)
        val dir = getDirFromAngle(get_angle(clicked_x,clicked_y,game))
        
        if( dir != null && clicked_x < game.first_floor.getWidth() && clicked_y < game.first_floor.getHeight() && game.occupied(game.player.rx+dir.x,game.player.ry+dir.y) ==false ){
          //game.mouse_dir = new Position(clicked_x,clicked_y)
          //panel.repaint()
          game.mouse_dir =  new Position(game.player.rx+dir.x,game.player.ry+dir.y)
          panel.repaint()
        }else{
          game.mouse_dir = null
          panel.repaint()
        }
        
      case MouseClicked(_,coord,_,_,_) => 
        var clicked_x = ((coord.getX() -vars.getDX())/(vars.getCURRENT_SIZE().toFloat)).toInt
        var clicked_y = ((coord.getY() -vars.getDY())/(vars.getCURRENT_SIZE().toFloat)).toInt
        //println("j'ai cliqué en "+clicked_x+"  "+clicked_y)
        //println("perso en: "+game.player.getX()+"  "+game.player.getY())
        if( clicked_x < game.first_floor.getWidth() && clicked_y < game.first_floor.getHeight() && game.mouse_dir != null){
          game.player.setX(game.mouse_dir.x)
          game.player.setY(game.mouse_dir.y)

          val dir = getDirFromAngle(get_angle(clicked_x,clicked_y,game))
        
          if( dir != null && clicked_x < game.first_floor.getWidth() && clicked_y < game.first_floor.getHeight() && game.occupied(game.player.rx+dir.x,game.player.ry+dir.y) ==false ){
            //game.mouse_dir = new Position(clicked_x,clicked_y)
            //panel.repaint()
            game.mouse_dir =  new Position(game.player.rx+dir.x,game.player.ry+dir.y)
          }else{
            game.mouse_dir = null
          }
          game.processDecisions()
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
  def get_angle(clicked_x: Int, clicked_y: Int, game: GameObject):Double = {
    val xa = 5
    val ya = 0
    val xb = clicked_x -game.player.rx
    val yb = clicked_y -game.player.ry
    if(xb == 0.0 && yb == 0.0){
      return 1000.0
    }
    return scala.math.acos((xa*xb+ya*yb)/(scala.math.sqrt(xa*xa + ya*ya)*scala.math.sqrt(xb*xb+yb*yb)))*180/scala.math.Pi * (if (yb>=0) -1 else 1) + (if (yb>=0) 360 else 0)
  }
  def getDirFromAngle(angle: Double): Position ={
    //println("angle: " , angle)
    if(angle >= 500){
      return null
    }else if(angle <= 22.5 || angle >= 347.5){
      return new Position(1,0)
    }else if(angle >= 22.5 && angle <= 77.5){
      return new Position(1,-1)
    }else if(angle >= 77.5 && angle <= 122.5){
      return new Position(0,-1)
    }else if(angle >= 122.5 && angle <= 167.5){
      return new Position(-1,-1)
    }else if(angle >= 167.5 && angle <= 212.5){
      return new Position(-1,0)
    }else if(angle >= 212.5 && angle <= 257.5){
      return new Position(-1,1)
    }else if(angle >= 257.5 && angle <= 302.5){
      return new Position(0,1)
    }else if(angle >= 302.5 && angle <= 347.5){
      return new Position(1,1)
    }
    return null
  }
}
