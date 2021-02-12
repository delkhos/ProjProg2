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
    g.fillRect(sx1.toInt,sy1.toInt, tileset_handler.getSize(), tileset_handler.getSize())
    g.drawImage(tileset_handler.getColoredTileset(fg), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)
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

  def drawMap(g: Graphics2D, wwidth: Int, width: Int, floor: Map ){
    val floor_grid = floor.getFloor()
    if (tileset_handler.isReady()){
      for(x: Int <- 0 to (floor.getWidth()-1) ){
        for(y: Int <- 0 to (floor.getHeight()-1) ){
          if(floor_grid(x)(y)==1){
            val l = if(x==0) false else floor_grid(x-1)(y) == 1
            val r = if(x==(floor.getWidth()-1)) false else floor_grid(x+1)(y) == 1
            val u = if(y==0) false else floor_grid(x)(y-1) == 1
            val d = if(y==(floor.getHeight()-1)) false else floor_grid(x)(y+1) == 1
            if(u && d && l && r){
              paintCharacter(g, 206, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(u && d && l){
              paintCharacter(g, 185, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(u && d && r){
              paintCharacter(g, 204, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(l && d && r){
              paintCharacter(g, 203, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(l && u && r){
              paintCharacter(g, 202, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(u && r){
              paintCharacter(g, 200, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(u && l){
              paintCharacter(g, 188, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(d && r){
              paintCharacter(g, 201, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(d && l){
              paintCharacter(g, 187, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(u && d ){
              paintCharacter(g, 186, x, y, Color.BLACK, "230230230", wwidth, width)
            }else
            if(l && r){
              paintCharacter(g, 205, x, y, Color.BLACK, "230230230", wwidth, width)
            }else{
              paintCharacter(g, 178, x, y, Color.BLACK, "230230230", wwidth, width)
            }
            
          }else{
            paintCharacter(g, 250, x, y, Color.BLACK, "120120120", wwidth, width)
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


  def drawGame(g: Graphics2D, wwidth: Int, wheight: Int,width: Int, height: Int, ui_width: Int, ui_height: Int,floor: Map ){
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)
    g.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED)

    clearScreen(g, wwidth, wheight)
    drawMap(g, wwidth, width, floor)
    drawUI(g, wwidth, width, height, ui_width, ui_height )
    
  }
}
