package rogue 

import java.awt.{Color,Graphics2D, Graphics}

class GameObject(width: Int, height: Int) { 
  var first_floor = new MapAutomata(width, height)
  var player = new Player(1, 0, new Sprite( Array[SubSprite](new SubSprite(256,"153051153")) , Color.BLACK), true, 20, 60, 10,"the Hero","000000255")
  var monsters: List[Monster] = List() 
  var mouse_dir: Position = null

  placePlayer()
  placeMonster(new Goblin(0,0))

  def placePlayer(){
    val r = scala.util.Random
    var x = r.nextInt(width)
    var y = r.nextInt(height)
    while((first_floor.getFloor())(x)(y).getBlocking() == true){
      x = r.nextInt(width)
      y = r.nextInt(height)
    }
    player.setX(x)
    player.setY(y)
  }
  def placeMonster(monster: Monster){
    val r = scala.util.Random
    var x = r.nextInt(width)
    var y = r.nextInt(height)
    while( occupied(x,y)== true || lineOfSight(x,y,player.getX(),player.getY())==true ){
      x = r.nextInt(width)
      y = r.nextInt(height)
    }
    monster.setX(x)
    monster.setY(y)
    monsters = monster :: monsters
  }


  def lineOfSight(x1: Int,y1: Int,x2: Int, y2: Int ): Boolean = {
    val deltaX = scala.math.abs(x1-x2)
    val deltaY = scala.math.abs(y1-y2)
    val signX = if((x2-x1)<0) -1 else 1
    val signY = if((y2-y1)<0) -1 else 1
    var x = x1
    var y = y1
    if(deltaX > deltaY){
      var t = deltaY*2-deltaX
      do{
        if(t>=0){
          y += signY
          t -= deltaX*2
        }
        x += signX
        t += deltaY*2
        if(x==x2 && y==y2){
          return true
        }
      }while(first_floor.getFloor()(x)(y).getBlocking()==false)
      return false
    }else{
      var t = deltaX*2-deltaY
      do{
        if(t>=0){
          x += signX
          t -= deltaY*2
        }
        y += signY
        t += deltaX*2
        if(x==x2 && y==y2){
          return true
        }
      }while(first_floor.getFloor()(x)(y).getBlocking()==false)
      return false
    }
  }
  def sortPos(p1: PositionPath, p2: PositionPath): Boolean = {
    return (p1.gcost + p1.hcost) < (p2.gcost + p2.hcost)
  }
  def a_star_path(x1: Int,y1: Int,x2: Int, y2: Int ): List[Position] = {
    val start_node = new PositionPath(x1,y1,null, 0, 0)
    val end_node = new PositionPath(x2,y2, null, Int.MaxValue, Int.MaxValue)
    var open_set = List(start_node)
    var closed_set = List[PositionPath]()
    var path = List[PositionPath]()
    var i = 0
    while(open_set.length > 0){
      i += 1
      //println(open_set)
      open_set = open_set.sortWith(sortPos)
      //println(open_set)
      var current_node = open_set.head
      open_set = open_set.tail
      //println(open_set)
      closed_set = current_node :: closed_set
      //println("travail sur x="+current_node.x+" y="+current_node.y)
      //Thread.sleep(1000)
      if( current_node == end_node){
      //if( current_node.x == end_node.x && current_node.y == end_node.y){
        //println("trouvé")
        while(current_node != start_node){
          path = current_node :: path
          current_node = current_node.parent
        }
        return path
      }
      for(i <- -1 to 1){
        for(j <- -1 to 1){
          if(i!=0 || j!=0){
            if((!occupied(current_node.x+i, current_node.y+j) || (end_node.x == (current_node.x+i) && end_node.y == (current_node.y+j)) ) && !closed_set.exists((p: PositionPath)=>p.x == (current_node.x+i) && p.y == (current_node.y+j))){
              //println("travail sur voisin x="+(current_node.x+i)+" y="+(current_node.y+j))
              var neighbour_node = open_set.find(p => p.x == (current_node.x+i) && p.y == (current_node.y+j)).getOrElse(
                new PositionPath(current_node.x + i, current_node.y+j,null,Int.MaxValue,Int.MaxValue)
              )
              var is_in_open_set = open_set.exists((p: PositionPath)=>p.x == (neighbour_node.x) && p.y == (neighbour_node.y))
              //println("neighbour : "+ neighbour_node)
              //println("is_in_open_set="+is_in_open_set)
              var cost = current_node.gcost + heuristic_cost_estimate(current_node,neighbour_node)
              if(cost < neighbour_node.gcost || !is_in_open_set){
                neighbour_node.gcost = cost
                neighbour_node.hcost = heuristic_cost_estimate(neighbour_node, end_node)
                neighbour_node.parent = current_node 
                if(!is_in_open_set){
                  open_set = neighbour_node :: open_set
                }
              }
            }
          }
        }
      }
    }
    //println("on a cherché " + i + "noeuds")

    return null
  }
  def heuristic_cost_estimate(nodeA: Position, nodeB: Position) : Int = {
    val deltaX = scala.math.abs(nodeA.x - nodeB.x);
    val deltaY = scala.math.abs(nodeA.y - nodeB.y);

    if (deltaX > deltaY)
      return 14 * deltaY + 10 * (deltaX - deltaY);
    return 14 * deltaX + 10 * (deltaY - deltaX);
}

  def newMap(){
    first_floor = new MapAutomata(width, height)
    placePlayer()
  }

  def getMap():MapAutomata = {
    return first_floor
  }
  def getPlayer():Entity = {
    return player
  }
  def occupied(x:Int, y:Int): Boolean = {
    return first_floor.getFloor()(x)(y).getBlocking==true || (x==player.getX() && y==player.getY()) || monsters.exists( (m: Monster)=>{
      return (x==m.getX() && y==m.getY())
    })
  }

  def processDecisions(){
    monsters.foreach((m: Monster)=> {
        m.processDecision(this)
        }
      )
  }
}
