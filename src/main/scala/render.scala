package rogue

import java.awt.{Color,Graphics2D, Graphics}

class Renderer {
  val tileset_handler = new TileSetHandler(16, "src/main/resources/3_16.png")
  def getTileSize():Int = {
    return tileset_handler.getSize()
  }

  def initIsOk(): Boolean = {
    return tileset_handler.isReady()
  }
  def paintCharacter(g: Graphics2D,c: Int, px: Int, py: Int, bg: Color, fg: String, current_size: Int, dx: Int, dy: Int){
    val x:Int = c % 16 
    val y:Int = c / 16 
    val dx1:Int = x*tileset_handler.getSize() 
    val dy1:Int = y*tileset_handler.getSize()
    val dx2:Int = dx1+(tileset_handler.getSize())
    val dy2:Int = dy1+(tileset_handler.getSize())
    val size:Float = current_size
    val sx1:Float = px*size+dx // -px 
    val sy1:Float = py*size+dy // -py 
    val sx2:Float = sx1 + (size)
    val sy2:Float = sy1 + (size)
    g.setColor(bg);
    g.fillRect(sx1.toInt,sy1.toInt, current_size, current_size)
    g.drawImage(tileset_handler.getColoredTileset(fg), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)
  }
  def paintCharacterGray(g: Graphics2D,c: Int, px: Int, py: Int, bg: Color, fg: String, current_size: Int, ratio: Double, dx: Int, dy: Int){


    val x:Int = c % 16 
    val y:Int = c / 16 
    val dx1:Int = x*tileset_handler.getSize() 
    val dy1:Int = y*tileset_handler.getSize()
    val dx2:Int = dx1+(tileset_handler.getSize())
    val dy2:Int = dy1+(tileset_handler.getSize())
    val size:Float = current_size
    val sx1:Float = px*size+dx // -px 
    val sy1:Float = py*size+dy // -py 
    val sx2:Float = sx1 + (size)
    val sy2:Float = sy1 + (size)
    val red = "%03d".format((fg.substring(0,3).toInt*ratio).toInt)
    val green = "%03d".format((fg.substring(3,6).toInt*ratio).toInt)
    val blue = "%03d".format((fg.substring(6,9).toInt*ratio).toInt)
    g.setColor(new Color( (bg.getRed() * ratio).toInt , (bg.getGreen() * ratio).toInt, (bg.getBlue() * ratio).toInt    ));
    g.fillRect(sx1.toInt,sy1.toInt, current_size, current_size)
    g.drawImage(tileset_handler.getColoredTileset(red+green+blue), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)

  }

  def drawString(g: Graphics2D, px: Int, py: Int, bg: Color, fg: String, current_size:Int,text: String){
    for(i <- 0 to (text.length()-1)){
      paintCharacter(g, (text.charAt(i)).toInt ,px+i,py,bg,fg,current_size,0,0)
    } 
  }

  def clearScreen(g: Graphics2D,matrix_width: Int, matrix_height:Int, current_size:Int){
    g.setColor(Color.BLACK);
    g.fillRect(0,0, matrix_width*current_size, matrix_height*current_size)
  }

  def drawRandomScreen(g: Graphics2D, current_size: Int, matrix_width: Int, matrix_height: Int){
    if (tileset_handler.isReady()){
      val r = scala.util.Random
      for(i <- 1 to matrix_width ){
        for(j <- 1 to matrix_height ){
          val c = r.nextInt(256)
          val red = "%03d".format(r.nextInt(11) * 25)
          val green = "%03d".format(r.nextInt(11) * 25)
          val blue = "%03d".format(r.nextInt(11) *25)
          paintCharacter(g, c , i, j,Color.BLACK, red+green+blue, current_size, 0 , 0)
        }
      }
    }else{
      println("Couldn't load tileset, exiting.\n")
      //System.exit(1)
    }
  }

  def drawMap(g: Graphics2D, current_size: Int, game: GameObject , dx: Int, dy: Int){
    val player = game.getPlayer()
    val floor = game.getMap()
    val floor_grid = game.getMap().getFloor()
    if (tileset_handler.isReady()){
      for(x: Int <- 0 to (floor.getWidth()-1) ){
        for(y: Int <- 0 to (floor.getHeight()-1) ){
          if(game.lineOfSight(player.getX(),player.getY(),x,y)){
            floor.getSeen()(x)(y)=1
            if(floor_grid(x)(y)==1){
              paintCharacter(g, 0, x, y, new Color(180,180,180), "180180180", current_size, dx, dy)
            }else{
              paintCharacter(g, 250, x, y, Color.BLACK, "180180180", current_size, dx, dy)
            }
          }else if(floor.getSeen()(x)(y)==1){
            if(floor_grid(x)(y)==1){
              paintCharacterGray(g, 0, x, y, new Color(180,180,180), "180180180",current_size, 0.5, dx, dy)
            }else{
              paintCharacterGray(g, 250, x, y, Color.BLACK, "180180180", current_size,0.5, dx, dy)
            }
          }
        }
      }
    }else{
      println("Couldn't load tileset, exiting.\n")
      //System.exit(1)
    }
  }
  def drawUI(g: Graphics2D, matrix_width: Int, matrix_height: Int, ui_width: Int, ui_height: Int){
    val size = tileset_handler.getSize()
    g.setColor(Color.BLACK);
    g.fillRect((matrix_width-ui_width-1)*size,0, matrix_width*size, matrix_height*size)
    g.fillRect(0,(matrix_height-ui_height-1)*size, matrix_width*size, matrix_height*size)
    if (tileset_handler.isReady()){
      for( j <- 0 to (matrix_height-ui_height-2) ){
        paintCharacter(g, 179, (matrix_width-ui_width-1) , j, Color.BLACK, "000050200", tileset_handler.getSize(), 0, 0)
      }
      for( j <- 0 to (matrix_width-ui_width-2) ){
        paintCharacter(g, 196, j, (matrix_height-ui_height-1), Color.BLACK, "000050200", tileset_handler.getSize(), 0, 0)
      }
      paintCharacter(g, 217, (matrix_width-ui_width-1), (matrix_height-ui_height-1), Color.BLACK, "000050200", tileset_handler.getSize() ,0,0)
    }else{
      println("Couldn't load tileset, exiting.\n")
      //System.exit(1)
    }
  }


  def drawGame(g: Graphics2D, current_size: Int, matrix_width: Int, matrix_height: Int, ui_width: Int, ui_height: Int,game: GameObject, dx: Int, dy: Int){
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)
    g.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED)

    clearScreen(g, matrix_width, matrix_height, tileset_handler.getSize())
    drawMap(g, current_size, game, dx, dy)
    game.getPlayer().draw(g,current_size, tileset_handler, dx, dy)
    drawUI(g, matrix_width, matrix_height, ui_width, ui_height )
    drawString(g, 0, 44, Color.BLACK, "255255255", tileset_handler.getSize(),"test")    
    for(i <- 0 to 44){
      //drawString(g, 0, i, Color.BLACK, "255255255", current_size,i.toString)    
      g.setColor(Color.WHITE);
      //g.fillRect(i*current_size,i*current_size, current_size, current_size)
    }
  }
}
