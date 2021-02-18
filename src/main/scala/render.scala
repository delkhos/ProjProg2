package rogue

import java.awt.{Color,Graphics2D, Graphics}

class Renderer {
  val tileset_handler = new TileSetHandler(16, "src/main/resources/3_16.png")

  def initIsOk(): Boolean = {
    return tileset_handler.isReady()
  }

  def paintCharacter(g: Graphics2D,c: Int, px: Int, py: Int, bg: Color, fg: String, wwidth: Int, width: Int){
    val x:Int = c % 16 
    val y:Int = c / 16 
    val dx1:Int = x*tileset_handler.getSize() 
    val dy1:Int = y*tileset_handler.getSize()
    val dx2:Int = dx1+(tileset_handler.getSize()-1)
    val dy2:Int = dy1+(tileset_handler.getSize()-1)
    val size:Float = wwidth.toFloat/width.toFloat
    val sx1:Float = px*size-px
    val sy1:Float = py*size-py
    val sx2:Float = sx1 + (size-1)
    val sy2:Float = sy1 + (size-1)
    g.setColor(bg);
    g.fillRect(sx1.toInt,sy1.toInt, tileset_handler.getSize()-1, tileset_handler.getSize()-1)
    g.drawImage(tileset_handler.getColoredTileset(fg), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)
  }
  def paintCharacterGray(g: Graphics2D,c: Int, px: Int, py: Int, bg: Color, fg: String, wwidth: Int, width: Int, ratio: Double){
    val x:Int = c % 16 
    val y:Int = c / 16 
    val dx1:Int = x*tileset_handler.getSize() 
    val dy1:Int = y*tileset_handler.getSize()
    val dx2:Int = dx1+(tileset_handler.getSize()-1)
    val dy2:Int = dy1+(tileset_handler.getSize()-1)
    val size:Float = wwidth.toFloat/width.toFloat
    val sx1:Float = px*size-px
    val sy1:Float = py*size-py
    val sx2:Float = sx1 + (size-1)
    val sy2:Float = sy1 + (size-1)
    val red = "%03d".format((fg.substring(0,3).toInt*ratio).toInt)
    val green = "%03d".format((fg.substring(3,6).toInt*ratio).toInt)
    val blue = "%03d".format((fg.substring(6,9).toInt*ratio).toInt)
    //println("Debug : " + red + "  " + green + "  " + blue + "\n")
    
    
    g.setColor(new Color( (bg.getRed() * ratio).toInt , (bg.getGreen() * ratio).toInt, (bg.getBlue() * ratio).toInt    ));
    g.fillRect(sx1.toInt,sy1.toInt, tileset_handler.getSize()-1, tileset_handler.getSize()-1)
    g.drawImage(tileset_handler.getColoredTileset(red+""+green+""+blue), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)
  }

  def drawString(g: Graphics2D, px: Int, py: Int, bg: Color, fg: String, wwidth: Int, width: Int, text: String){
    for(i <- 0 to (text.length()-1)){
      paintCharacter(g, (text.charAt(i)).toInt ,px+i,py,bg,fg,wwidth,width)
    } 
  }

  def clearScreen(g: Graphics2D,wwidth: Int, wheight: Int){
    g.setColor(Color.BLACK);
    g.fillRect(0,0, wwidth, wheight)
  }

  def drawRandomScreen(g: Graphics2D, wwidth: Int, width: Int, height: Int){
    if (tileset_handler.isReady()){
      val r = scala.util.Random
      for(i <- 1 to width ){
        for(j <- 1 to height ){
          val c = r.nextInt(256)
          val red = "%03d".format(r.nextInt(11) * 25)
          val green = "%03d".format(r.nextInt(11) * 25)
          val blue = "%03d".format(r.nextInt(11) *25)
          paintCharacter(g, c , i, j,Color.BLACK, red+green+blue, wwidth, width)
        }
      }
    }else{
      println("Couldn't load tileset, exiting.\n")
      //System.exit(1)
    }
  }

  def drawMap(g: Graphics2D, wwidth: Int, width: Int, game: GameObject ){
    val player = game.getPlayer()
    val floor = game.getMap()
    val floor_grid = game.getMap().getFloor()
    if (tileset_handler.isReady()){
      for(x: Int <- 0 to (floor.getWidth()-1) ){
        for(y: Int <- 0 to (floor.getHeight()-1) ){
          if(game.lineOfSight(player.getX(),player.getY(),x,y)){
            floor.getSeen()(x)(y)=1
            if(floor_grid(x)(y)==1){
              paintCharacter(g, 0, x, y, new Color(180,180,180), "180180180", wwidth, width)
            }else{
              paintCharacter(g, 250, x, y, Color.BLACK, "180180180", wwidth, width)
            }
          }else if(floor.getSeen()(x)(y)==1){
            if(floor_grid(x)(y)==1){
              paintCharacterGray(g, 0, x, y, new Color(180,180,180), "180180180", wwidth, width, 0.5)
            }else{
              paintCharacterGray(g, 250, x, y, Color.BLACK, "180180180", wwidth, width,0.5)
            }
          }
        }
      }
    }else{
      println("Couldn't load tileset, exiting.\n")
      //System.exit(1)
    }
  }
  def drawUI(g: Graphics2D, wwidth: Int, width: Int, height: Int, ui_width: Int, ui_height: Int){
    if (tileset_handler.isReady()){
      for( j <- 0 to (height-ui_height-2) ){
        paintCharacter(g, 179, (width-ui_width-1) , j, Color.BLACK, "000050200", wwidth, width)
      }
      for( j <- 0 to (width-ui_width-2) ){
        paintCharacter(g, 196, j, (height-ui_height-1), Color.BLACK, "000050200", wwidth,width)
      }
      paintCharacter(g, 217, (width-ui_width-1), (height-ui_height-1), Color.BLACK, "000050200", wwidth,width)
    }else{
      println("Couldn't load tileset, exiting.\n")
      //System.exit(1)
    }
  }


  def drawGame(g: Graphics2D, wwidth: Int, wheight: Int,width: Int, height: Int, ui_width: Int, ui_height: Int,game: GameObject){
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)
    g.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED)

    clearScreen(g, wwidth, wheight)
    drawMap(g, wwidth, width, game)
    drawUI(g, wwidth, width, height, ui_width, ui_height )
    game.getPlayer().draw(g,wwidth,wheight,width,height, tileset_handler)
    drawString(g, 0, 47, Color.BLACK, "255255255", wwidth,width,"Ceci est un test")
    
  }
}
