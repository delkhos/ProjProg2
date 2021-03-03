package rogue 

import swing.event._
import swing._
import java.awt.event.KeyEvent

/* This function is called by the GamePanel, when a user input occurs
 * It is the MainLoop of our game, since it is not real time
 */
object MainLoopObject{ //object containing the main loop that runs the game
  def mainLoop(panel:GamePanel, vars:MainLoopVars, renderer: Renderer, game: GameObject, e: Event, game_matrix_dim: Dimension, matrix_dim: Dimension,ui_dim: Dimension){
    if (game.player.health<=0){ //ends the game if the player dies
      if(!game.lose){
        game.lose = true
        Log.addLogMessage( new LogMessage( List( new SubMessage(" YOU LOSE ", "255000000")) ))
        panel.repaint()
      }
    }else if(game.trophy){ //ends the game if the player wins
      if(!game.win){
        game.win=true
        Log.addLogMessage( new LogMessage( List( new SubMessage(" YOU WIN ","255255000") )))
        Log.addLogMessage( new LogMessage( List(
          new SubMessage(" C","204000204"), 
          new SubMessage("O","153000255"), 
          new SubMessage("N","102000255"),
          new SubMessage("G","102051255"),
          new SubMessage("R","102163255"),
          new SubMessage("A","102204204"), 
          new SubMessage("T","102255051"),
          new SubMessage("U","102255000"),
          new SubMessage("L","255255000"),
          new SubMessage("A","255204000"),
          new SubMessage("T","255102000"),
          new SubMessage("I","255051000"), 
          new SubMessage("O","255025000"),
          new SubMessage("N","255000000")
        ) ) )
        panel.repaint()

      }
    }else{
      e match { // respond to the key pressed and apply the corresponding decision if there is such
        case KeyPressed(_,k,_,_) =>  //keyboard control
          game.mouse_dir = null
          if(k == Key.Asterisk){ //resize the screen to the initial value
            vars.current_size = renderer.getTileSize()
            vars.dpos.x = 0.0f
            vars.dpos.y = 0.0f
            panel.repaint()
            println("Current resolution : size="+vars.current_size)
          }else if(k == Key.Up){ //moves the player a tile up,
            if(!game.occupied(game.player.pos.translate(0,-1))){
              game.player.pos.y += -1
              game.processDecisions()
              panel.repaint()
            }else{ //or attack the monster that occupies the tile, if there is one
              var monsteropt = game.monsters.find(m => (m.pos == game.player.pos.translate(0,-1)))
              monsteropt match{
                case Some(targetMonster) => {
                  game.player.attack(targetMonster)
                  game.processDecisions() // go to the next turn
                  panel.repaint()
              
                }
                case None => {} // if the movement can not be applied, the turn continues
              }
            }
          }else if(k == Key.Down){ // similar to Up
            if(!game.occupied(game.player.pos.translate(0,1))){
              game.player.pos.y += 1
              game.processDecisions()
              panel.repaint()
            }else{ 
              var monsteropt = game.monsters.find(m => (m.pos == game.player.pos.translate(0,1)))
              monsteropt match{
                case Some(targetMonster) => {
                  game.player.attack(targetMonster)
                  game.processDecisions()
                  panel.repaint()
              }
                case None => {}
              }            
            }
          }else if(k == Key.Left){
            if(!game.occupied(game.player.pos.translate(-1,0))){
              game.player.pos.x += -1
              game.processDecisions()
              panel.repaint()
            }else{ 
              var monsteropt = game.monsters.find(m => (m.pos == game.player.pos.translate(-1,0)))
              monsteropt match{
                case Some(targetMonster) => {
                  game.player.attack(targetMonster)
                  game.processDecisions()
                  panel.repaint()
                
                }
                case None => {}
              }
            }
  
          }else if(k == Key.Right){
            if(!game.occupied(game.player.pos.translate(1,0))){
              game.player.pos.x += 1
              game.processDecisions()
              panel.repaint()
            } 
            else{ 
              var monsteropt = game.monsters.find(m => (m.pos == game.player.pos.translate(1,0)))
              monsteropt match{
                case Some(targetMonster) => {
                  game.player.attack(targetMonster)
                  game.processDecisions()
                  panel.repaint()
                }
                case None => {}
              }
            }
          }else if(k == Key.W){ // skip the player's turn
            // used to "wait"
            game.player.waitAction
            game.processDecisions()
            panel.repaint()
          }else{} //continues the turn if the key does not corresponds to any move

        case MousePressed(_,coord,_,_,_) => // starts a dragging event, that allow the player to see the map freely
          vars.dragging = true

        case MouseMoved(_,coord,_) => //detects where the mouse is, relative to the player, and executes controls similarly to Key.Up, in the direction ofthe mouse
          var clicked_x = ((coord.x -vars.dpos.x)/(vars.current_size.toFloat)).toInt
          var clicked_y = ((coord.y -vars.dpos.y)/(vars.current_size.toFloat)).toInt
          val dir = getDirFromAngle(get_angle(clicked_x,clicked_y,game))
          
          if( dir != null && clicked_x < game.first_floor.dim.width && clicked_y < game.first_floor.dim.height && game.occupied(game.player.pos.translate(dir.x,dir.y)) ==false ){
            game.mouse_dir = game.player.pos.translate(dir.x,dir.y)
            game.attack_dir = game.player.pos.translate(dir.x,dir.y)
            panel.repaint()
          }else if(dir != null){
            game.mouse_dir = null
            game.attack_dir = game.player.pos.translate(dir.x,dir.y)
            panel.repaint()
          }else{
            game.mouse_dir = null
            game.attack_dir = null
          }
          
        case MouseClicked(_,coord,_,_,_) => //determines if the player has clicked on a gametile, or on the ui and executes the corresponding action 
          // allow both movements and attacks of the player using the mouse
          // For now it is the only way to walk diagonaly
          var clicked_x = ((coord.x -vars.dpos.x)/(vars.current_size.toFloat)).toInt
          var clicked_y = ((coord.y -vars.dpos.y)/(vars.current_size.toFloat)).toInt

          var ui_clicked_x = ((coord.x -vars.dpos.x)/(renderer.tileset_handler.getSize().toFloat)).toInt
          var ui_clicked_y = ((coord.y -vars.dpos.y)/(renderer.tileset_handler.getSize().toFloat)).toInt
          val istart = (matrix_dim.width-ui_dim.width)
          val iend = (matrix_dim.width-1)
          val jstart = (matrix_dim.height-ui_dim.height-1-(game.player.inventory.size/(ui_dim.width-2)+1)-1)
          val jend = (matrix_dim.height-ui_dim.height-1)
          val inventory_coord = ui_clicked_x-(istart+1)+(ui_clicked_y-(jstart+1))*(iend-istart-1)
  
          if( clicked_x < game.first_floor.dim.width && clicked_y < game.first_floor.dim.height && (game.mouse_dir != null || game.attack_dir != null)){ //the player has clicked on a gametile
            var monsteropt = game.monsters.find(m => (m.pos == game.attack_dir))
            monsteropt match{ //toward a monster within the attack range
              case Some(targetMonster) => {
                game.player.attack(targetMonster)
                game.processDecisions()
                panel.repaint()
              }
              case None => { //toward an empty tile
                if(game.mouse_dir != null){
                  game.player.pos.x = game.mouse_dir.x
                  game.player.pos.y = game.mouse_dir.y
                  game.processDecisions()
                  panel.repaint()
                }
              }
  
              val dir = getDirFromAngle(get_angle(clicked_x,clicked_y,game)) // if the player has not clicked on an adjacent tile, considers the tile in the direction of the mouse
            
              if(dir !=null && clicked_x < game.first_floor.dim.width && clicked_y < game.first_floor.dim.height && game.occupied(game.player.pos.translate(dir.x,dir.y))==false ){
                game.mouse_dir =  game.player.pos.translate(dir.x,dir.y)
                game.attack_dir = game.player.pos.translate(dir.x,dir.y)
              }else if(dir != null){
                game.mouse_dir = null
                game.attack_dir = game.player.pos.translate(dir.x,dir.y)
              }else{
                game.mouse_dir = null
                game.attack_dir = null
              }
            }
            }else if(inventory_coord>= 0 && inventory_coord < game.player.inventory.size && game.player.inventory.contents(inventory_coord) != null){ // the player has clicked on the inventory
            game.player.inventory.contents(inventory_coord).use(game)
            game.processDecisions()
            panel.repaint()
          }

          case MouseDragged(_,coord,_) => // move the map according to the drag
          val sx2 = coord.x
          val sy2 = coord.y
          if(vars.dragging){
            vars.spos.x = sx2
            vars.spos.y = sy2
            vars.dragging = false
          }
          vars.dpos.x +=  sx2-vars.spos.x 
          vars.dpos.y +=  sy2-vars.spos.y
          vars.spos.x = sx2
          vars.spos.y = sy2
          vars.center_pos.x = (renderer.getTileSize()*game_matrix_dim.width/2 -vars.dpos.x)/(vars.current_size.toFloat)
          vars.center_pos.y = (renderer.getTileSize()*game_matrix_dim.height/2 -vars.dpos.y)/(vars.current_size.toFloat)
          panel.repaint()
        case MouseReleased(_,coord,_,_,_) => // ends the dragging event
          vars.dragging = false
        case MouseWheelMoved(_,coord,_,n) => // zooms in and out on the map 
          val old = vars.current_size
          vars.current_size = old+n
          if(vars.current_size<renderer.getTileSize()){
            vars.current_size = renderer.getTileSize()
          }else{
            vars.dpos = vars.dpos.translate(- n*vars.center_pos.x, - n*vars.center_pos.y)
          }
          panel.repaint()
        case _ => {}
      }
    }
  }
  def get_angle(clicked_x: Int, clicked_y: Int, game: GameObject):Double = { //returns the angle of the mouse relative to the object [player] and the horizontal line
    val xa = 5
    val ya = 0
    val xb = clicked_x -game.player.pos.x
    val yb = clicked_y -game.player.pos.y
    if(xb == 0.0 && yb == 0.0){
      return 1000.0
    }
    return scala.math.acos((xa*xb+ya*yb)/(scala.math.sqrt(xa*xa + ya*ya)*scala.math.sqrt(xb*xb+yb*yb)))*180/scala.math.Pi * (if (yb>=0) -1 else 1) + (if (yb>=0) 360 else 0)
  }
  def getDirFromAngle(angle: Double): Position ={ //converts an angle to direction to a tile nearby the player
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
