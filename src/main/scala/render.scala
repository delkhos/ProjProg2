package rogue

import java.awt.{Color,Graphics2D, Graphics}

class Renderer {
  val tileset_handler = new TileSetHandler(16, "src/main/resources/spritetable.png")
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
  def min(m:Int, n: Int): Int = {
    return if (m<=n) m else n
  }

  def drawString(g: Graphics2D, px: Int, py: Int, bg: Color, fg: String, current_size:Int,text: String){
    for(i <- 0 to (text.length()-1)){
      paintCharacter(g, (text.charAt(i)).toInt ,px+i,py,bg,fg,current_size,0,0)
    } 
  }
  def printLog(g: Graphics2D, matrix_height: Int, ui_height: Int, current_size: Int){
    val n = min(ui_height - 1, Log.messages.length - 1 )
    for(i <- 0 to n)
    {
      var m = Log.messages(i)
      for(j <- 0 to (m.sub_messages.length-1)){
        if(j==0){
          drawString(g,0, matrix_height-1-i, Color.BLACK, m.sub_messages(j).color, current_size, m.sub_messages(j).text)
        }else{
          var delta = 0
          for(k <- 0 to (j-1)){
            delta += m.sub_messages(k).text.length
          }
          drawString(g,delta, matrix_height-1-i, Color.BLACK, m.sub_messages(j).color, current_size, m.sub_messages(j).text)
        }
      }
      
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
    for(x: Int <- 0 to (floor.getWidth()-1) ){
      for(y: Int <- 0 to (floor.getHeight()-1) ){
        if(game.lineOfSight(player.getX(),player.getY(),x,y)){
        //if(true){
          floor.getSeen()(x)(y)=1
          floor_grid(x)(y).draw(g,current_size, tileset_handler,dx,dy,x,y)
          //////
          //println(floor_grid(x)(y))
        }else if(floor.getSeen()(x)(y)==1){
          floor_grid(x)(y).draw(g,current_size, tileset_handler,dx,dy,x,y,0.5)
        }
      }
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
    //draw monsters 
    game.items.foreach( (itm: Item) => if (game.lineOfSight(game.player.rx, game.player.ry, itm.rx, itm.ry)) itm.draw(g, current_size, tileset_handler,dx,dy) )
    game.monsters.foreach( (m: Monster) => if (game.lineOfSight(game.player.rx, game.player.ry, m.rx, m.ry)) m.draw(g, current_size, tileset_handler,dx,dy) )
    game.getPlayer().draw(g,current_size, tileset_handler, dx, dy)
    if(game.mouse_dir != null){
        paintCharacter(g, 9, game.mouse_dir.x, game.mouse_dir.y, new Color(1.0f,1.0f,1.0f,0.0f), "240240068", current_size, dx, dy)
    }
    drawUI(g, matrix_width, matrix_height, ui_width, ui_height )
    drawString(g, matrix_width-ui_width, 0, Color.BLACK, "255255255", tileset_handler.getSize(),"HP:"+game.player.health+"/"+game.player.max_health)    
    printLog(g, matrix_height,ui_height, tileset_handler.getSize)
  }
}
