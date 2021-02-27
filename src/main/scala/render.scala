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
  def paintCharacter(g: Graphics2D,c: Int, pos: Position, bg: Color, fg: String, current_size: Int, dpos: DPosition){
    val x:Int = c % 16 
    val y:Int = c / 16 
    val dx1:Int = x*tileset_handler.getSize() 
    val dy1:Int = y*tileset_handler.getSize()
    val dx2:Int = dx1+(tileset_handler.getSize())
    val dy2:Int = dy1+(tileset_handler.getSize())
    val size:Float = current_size
    val sx1:Float = pos.x*size+dpos.x // -px 
    val sy1:Float = pos.y*size+dpos.y // -py 
    val sx2:Float = sx1 + (size)
    val sy2:Float = sy1 + (size)
    g.setColor(bg);
    g.fillRect(sx1.toInt,sy1.toInt, current_size, current_size)
    g.drawImage(tileset_handler.getColoredTileset(fg), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)
  }
  def paintCharacterGray(g: Graphics2D,c: Int, pos: Position, bg: Color, fg: String, current_size: Int, ratio: Double, dpos: DPosition){


    val x:Int = c % 16 
    val y:Int = c / 16 
    val dx1:Int = x*tileset_handler.getSize() 
    val dy1:Int = y*tileset_handler.getSize()
    val dx2:Int = dx1+(tileset_handler.getSize())
    val dy2:Int = dy1+(tileset_handler.getSize())
    val size:Float = current_size
    val sx1:Float = pos.x*size+dpos.x // -px 
    val sy1:Float = pos.y*size+dpos.y // -py 
    val sx2:Float = sx1 + (size)
    val sy2:Float = sy1 + (size)
    val red = "%03d".format((fg.substring(0,3).toInt*ratio).toInt)
    val green = "%03d".format((fg.substring(3,6).toInt*ratio).toInt)
    val blue = "%03d".format((fg.substring(6,9).toInt*ratio).toInt)
    g.setColor(new Color( ((bg.getRed() * ratio) / 255.0).toFloat , ((bg.getGreen() * ratio)/255.0).toFloat, ((bg.getBlue() * ratio)/ 255.0).toFloat, (bg.getAlpha()/255.0).toFloat   ));
    g.fillRect(sx1.toInt,sy1.toInt, current_size, current_size)
    g.drawImage(tileset_handler.getColoredTileset(red+green+blue), sx1.toInt, sy1.toInt, sx2.toInt, sy2.toInt, dx1, dy1, dx2, dy2, null)

  }
  def min(m:Int, n: Int): Int = {
    return if (m<=n) m else n
  }

  def drawString(g: Graphics2D, pos: Position, bg: Color, fg: String, current_size:Int,text: String){
    for(i <- 0 to (text.length()-1)){
      paintCharacter(g, (text.charAt(i)).toInt ,pos.translate(i,0),bg,fg,current_size,DOrigin)
    } 
  }
  def printLog(g: Graphics2D, matrix_dim: Dimension, ui_dim: Dimension, current_size: Int){
    val n = min(ui_dim.height - 3, Log.messages.length - 1 )
    for(i <- 0 to n)
    {
      var m = Log.messages(i)
      for(j <- 0 to (m.sub_messages.length-1)){
        if(j==0){
          drawString(g,new Position(0, matrix_dim.height-1-i), Color.BLACK, m.sub_messages(j).color, current_size, m.sub_messages(j).text)
        }else{
          var delta = 0
          for(k <- 0 to (j-1)){
            delta += m.sub_messages(k).text.length
          }
          drawString(g,new Position(delta, matrix_dim.height-1-i), Color.BLACK, m.sub_messages(j).color, current_size, m.sub_messages(j).text)
        }
      }
      
    }
  }

  def clearScreen(g: Graphics2D,matrix_dim:Dimension, current_size:Int){
    g.setColor(Color.BLACK);
    g.fillRect(0,0, matrix_dim.width*current_size, matrix_dim.height*current_size)
  }

  def drawRandomScreen(g: Graphics2D, current_size: Int, matrix_dim: Dimension){
    val r = scala.util.Random
    for(i <- 1 to matrix_dim.width ){
      for(j <- 1 to matrix_dim.height ){
        val c = r.nextInt(256)
        val red = "%03d".format(r.nextInt(11) * 25)
        val green = "%03d".format(r.nextInt(11) * 25)
        val blue = "%03d".format(r.nextInt(11) *25)
        paintCharacter(g, c , new Position(i,j),Color.BLACK, red+green+blue, current_size, DOrigin)
      }
    }
  }

  def drawMap(g: Graphics2D, current_size: Int, game: GameObject , dpos: DPosition){
    val player = game.getPlayer()
    val floor = game.getMap()
    val floor_grid = game.getMap().getFloor()
    for(x: Int <- 0 to (floor.dim.width-1) ){
      for(y: Int <- 0 to (floor.dim.height-1) ){
        val pos = new Position(x,y)
        if(game.lineOfSight(player.pos,pos)){
        //if(true){
          floor.getSeen()(x)(y)=1
          floor_grid(x)(y).draw(g,current_size, tileset_handler,dpos,pos)
          //////
          //println(floor_grid(x)(y))
        }else if(floor.getSeen()(x)(y)==1){
          floor_grid(x)(y).draw(g,current_size, tileset_handler,dpos,pos,0.5)
        }
      }
    }
  }
  def drawUI(g: Graphics2D, matrix_dim: Dimension, ui_dim: Dimension, game: GameObject){
    // draw lines
    val size = tileset_handler.getSize()
    g.setColor(Color.BLACK);
    g.fillRect((matrix_dim.width-ui_dim.width-1)*size,0, matrix_dim.width*size, matrix_dim.height*size)
    g.fillRect(0,(matrix_dim.height-ui_dim.height-1)*size, matrix_dim.width*size, matrix_dim.height*size)
    for( j <- 0 to (matrix_dim.height-ui_dim.height-2) ){
      paintCharacter(g, 179, new Position(matrix_dim.width-ui_dim.width-1, j), Color.BLACK, "000050200", tileset_handler.getSize(), DOrigin)
    }
    for( j <- 0 to (matrix_dim.width-ui_dim.width-2) ){
      paintCharacter(g, 196, new Position( j, matrix_dim.height-ui_dim.height-1), Color.BLACK, "000050200", tileset_handler.getSize(), DOrigin)
    }
    paintCharacter(g, 217, new Position(matrix_dim.width-ui_dim.width-1, matrix_dim.height-ui_dim.height-1), Color.BLACK, "000050200", tileset_handler.getSize() , DOrigin)
    // draw hps
    drawString(g,new Position( matrix_dim.width-ui_dim.width, 0) , Color.BLACK, "255255255", tileset_handler.getSize(),"HP:"+game.player.health+"/"+game.player.max_health)    
    // draw log title
    drawString(g,new Position(0, matrix_dim.height-ui_dim.height) , Color.BLACK, "255255255", tileset_handler.getSize(),"Last events : " + (25).toChar)    
    // draw inventory
    val istart = (matrix_dim.width-ui_dim.width)
    val iend = (matrix_dim.width-1)
    val jstart = (matrix_dim.height-ui_dim.height-1-(game.player.inventory.size/(ui_dim.width-2)+1)-1)
    val jend = (matrix_dim.height-ui_dim.height-1)
    drawString(g,new Position(istart, jstart-1) , Color.BLACK, "255255255", tileset_handler.getSize(),"Inventory : ")    
    for(i <- istart  to iend){
      for(j <- jstart to jend ){
        val inventory_coord = i-(istart+1)+(j-(jstart+1))*(iend-istart-1)
        if(i== istart && j == jstart){
          paintCharacter(g,214, new Position(i, j), Color.BLACK, "230230230", tileset_handler.getSize() , DOrigin)
        }else if(i==iend && j==jstart){
          paintCharacter(g,183, new Position(i, j), Color.BLACK, "230230230", tileset_handler.getSize() , DOrigin)
        }else if(i==iend && j==jend){
          paintCharacter(g,189, new Position(i, j), Color.BLACK, "230230230", tileset_handler.getSize() , DOrigin)
        }else if(i==istart && j==jend){
          paintCharacter(g,211, new Position(i, j), Color.BLACK, "230230230", tileset_handler.getSize() , DOrigin)
        }else if(i==iend || i==istart){
          paintCharacter(g,186, new Position(i, j), Color.BLACK, "230230230", tileset_handler.getSize() , DOrigin)
        }else if(j==jend || j==jstart){
          paintCharacter(g,196, new Position(i, j), Color.BLACK, "230230230", tileset_handler.getSize() , DOrigin)
        }else if(inventory_coord >=0 && inventory_coord < game.player.inventory.size && game.player.inventory.contents(inventory_coord) != null ) {
          // draw item
          game.player.inventory.contents(inventory_coord).draw(g,tileset_handler.getSize(),tileset_handler, DOrigin, new Position(i,j))
        }else if(inventory_coord >=0 && inventory_coord < game.player.inventory.size && game.player.inventory.contents(inventory_coord) == null) {
          paintCharacter(g,('Z').toInt + 1, new Position(i, j), Color.BLACK, "120120120", tileset_handler.getSize() , DOrigin)
          paintCharacter(g,('Z').toInt + 3, new Position(i, j), new Color(1.0f,1.0f,1.0f,0.0f), "120120120", tileset_handler.getSize() , DOrigin)
        }else{
          paintCharacter(g,('Z').toInt + 2, new Position(i, j), Color.BLACK, "230230230", tileset_handler.getSize() , DOrigin)
          paintCharacter(g,('0').toInt - 1, new Position(i, j), new Color(1.0f,1.0f,1.0f,0.0f), "230230230", tileset_handler.getSize() , DOrigin)
        }
        //afficher l'objet et les cases non remplissable.
      }
    }
  }

  def drawGame(g: Graphics2D, current_size: Int, matrix_dim: Dimension, ui_dim: Dimension,game: GameObject, dpos: DPosition){
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)
    g.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED)

    clearScreen(g, matrix_dim, tileset_handler.getSize())
    drawMap(g, current_size, game, dpos)
    //draw monsters 
    game.items.foreach( (itm: Item) => itm.draw(g, current_size, tileset_handler,dpos,game) )
    game.monsters.foreach( (m: Monster) => m.draw(g, current_size, tileset_handler,dpos, game) )
    game.getPlayer().draw(g,current_size, tileset_handler, dpos)
    if(game.mouse_dir != null){
        paintCharacter(g, 9, game.mouse_dir, new Color(1.0f,1.0f,1.0f,0.0f), "240240068", current_size, dpos)
    }
    drawUI(g, matrix_dim, ui_dim, game)
    printLog(g, matrix_dim,ui_dim, tileset_handler.getSize)
  }
}
