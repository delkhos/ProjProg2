package rogue 

import java.awt.{Color,Graphics2D, Graphics}

class GameObject(dim: Dimension) { 
  var first_floor = new MapAutomata(dim)
  var player = new Player(Origin, new Sprite( Array[SubSprite](new SubSprite(2,"153051153")) , new Color(1.0f,1.0f,1.0f,0.0f)), true, 20, 60, 10,"the Hero","000000255")
  var monsters: List[Monster] = List() 
  var items: List[Item] = List()
  var mouse_dir: Position = null
  var attack_dir: Position = null

  placePlayer()
  placeMonster(new Goblin(Origin))
  for(i <- 0 to 20){
    placeItem(new HealingGoo(Origin))
  }

  def placePlayer(){
    val r = scala.util.Random
    val pos = new Position(r.nextInt(dim.width),r.nextInt(dim.height))
    while(occupied(pos) == true){
      pos.x = r.nextInt(dim.width)
      pos.y = r.nextInt(dim.height)
    }
    player.pos = pos
  }
  def placeMonster(monster: Monster){
    val r = scala.util.Random
    val pos = new Position(r.nextInt(dim.width),r.nextInt(dim.height))
    while( occupied(pos)== true || lineOfSight(pos,player.pos)==true ){
      pos.x = r.nextInt(dim.width)
      pos.y = r.nextInt(dim.height)
    }
    monster.pos = pos
    monsters = monster :: monsters
  }
  def placeItem(item: Item){
    val r = scala.util.Random
    val pos = new Position(r.nextInt(dim.width),r.nextInt(dim.height))
    while( occupied_item(pos)== true ){
      pos.x = r.nextInt(dim.width)
      pos.y = r.nextInt(dim.height)
    }
    item.pos = pos
    items = item :: items
  }
  def occupied_item(pos: Position):Boolean = {
    return first_floor.getFloor()(pos.x)(pos.y).getBlocking==true || items.exists( (itm: Item)=>{
      return (pos == itm.pos )
    })
  }


  def lineOfSight(pos1: Position, pos2: Position ): Boolean = {
    val deltaX = scala.math.abs(pos1.x-pos2.x)
    val deltaY = scala.math.abs(pos1.y-pos2.y)
    val signX = if((pos2.x-pos1.x)<0) -1 else 1
    val signY = if((pos2.y-pos1.y)<0) -1 else 1
    var x = pos1.x
    var y = pos1.y
    if(deltaX == deltaY && deltaY == 0){
      return true
    }
    if(deltaX > deltaY){
      var t = deltaY*2-deltaX
      do{
        if(t>=0){
          y += signY
          t -= deltaX*2
        }
        x += signX
        t += deltaY*2
        if(x==pos2.x && y==pos2.y){
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
        if(x==pos2.x && y==pos2.y){
          return true
        }
      }while(first_floor.getFloor()(x)(y).getBlocking()==false)
      return false
    }
  }
  def sortPos(p1: PositionPath, p2: PositionPath): Boolean = {
    return (p1.gcost + p1.hcost) < (p2.gcost + p2.hcost)
  }
  def a_star_path(pos1: Position, pos2: Position ): List[Position] = {
    val start_node = new PositionPath(pos1.x,pos1.y,null, 0, 0)
    val end_node = new PositionPath(pos2.x,pos2.y, null, Int.MaxValue, Int.MaxValue)
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
            if((!occupied(current_node.translate(i,j)) || (end_node.x == (current_node.x+i) && end_node.y == (current_node.y+j)) ) && !closed_set.exists((p: PositionPath)=>p.x == (current_node.x+i) && p.y == (current_node.y+j))){
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
    first_floor = new MapAutomata(dim)
    placePlayer()
  }

  def getMap():MapAutomata = {
    return first_floor
  }
  def getPlayer():Entity = {
    return player
  }
  def occupied(pos: Position): Boolean = {
    return first_floor.getFloor()(pos.x)(pos.y).getBlocking==true || (pos==player.pos) || monsters.exists( (m: Monster)=>{
      return pos==m.pos
    })
  }
  
  def processDecisions(){
    items.foreach((itm: Item)=> {
      itm.pickUp(this)
    }
    )
    // filtering items that are not on the ground
    items = items.filter( itm =>{ 
      itm.on_the_ground == true })
    monsters.foreach((m: Monster)=> {
        m.processDecision(this)
        }
      )
    // filtering dead monsters
    monsters = monsters.filter( m => m.state!=State.Dead)
  }
}

