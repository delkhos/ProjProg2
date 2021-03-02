package rogue 

import java.awt.{Color,Graphics2D, Graphics}


/*
 * This class is in charge of all the information that defines a game.
 * It has the responsibility to instanciate the player, and the map when created.
 * It also has all of the informations about the monsters and items.
 * It also keeps track of the state of the game.
 */
class GameObject(dim: Dimension) { 
  var first_floor = new MapAutomata(dim)
  var player = new Player(Origin, new Sprite( Array[SubSprite](new SubSprite(256,"230051153")) , new Color(1.0f,1.0f,1.0f,0.0f)), true, 20, 60, 10,"the Hero","000000255")
  var monsters: List[Monster] = List() 
  var items: List[Item] = List()
  var mouse_dir: Position = null
  var attack_dir: Position = null
  var trophySpawned: Boolean = false
  var win = false
  var lose = false
  var trophy = false

  // for now we palce the hero, and 4 monsters
  // as well as twenty items.
  // All ennemies must be vanquished to win the game.
  placePlayer()
  placeMonster(new Goblin(Origin))
  placeMonster(new Hunter(Origin))
  placeMonster(new Hoblin(Origin))
  placeMonster(new Bat(Origin))
  for(i <- 0 to 20){
    placeItem(new HealingGoo(Origin))
  }

  // This function is used to place the player at a random spot
  def placePlayer(){
    val r = scala.util.Random
    val pos = new Position(r.nextInt(dim.width),r.nextInt(dim.height))
    while(occupied(pos) == true){
      pos.x = r.nextInt(dim.width)
      pos.y = r.nextInt(dim.height)
    }
    player.pos = pos
  }
  // This function is used to place a monster at a random spot where it cannot be seen by the player
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

  // This function is used to place an item at a random spot
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
  // This function is used to test is a position is already occupied by item
  // , so as to avoid item stacking
  def occupied_item(pos: Position):Boolean = {
    return first_floor.getFloor()(pos.x)(pos.y).getBlocking || items.exists( (itm: Item)=>{
      return (pos == itm.pos )
    })
  }


  // This is the line of sight function, used to test if two positions can see each other.
  // It uses Bresenham's line algorithme, to trace a line between the points, if it gets interrupted
  // by a wall or other sight blocking entity, then it returns false. Otherwise it returns true.
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
  // This is a helper function used to sort PositionPath
  // in the A* algorithm.
  def sortPos(p1: PositionPath, p2: PositionPath): Boolean = {
    return (p1.gcost + p1.hcost) < (p2.gcost + p2.hcost)
  }
  // This is the path finding algorithm, which is an implementation of the A* algorithm.
  def a_star_path(pos1: Position, pos2: Position ): List[Position] = {
    val start_node = new PositionPath(pos1.x,pos1.y,null, 0, 0)
    val end_node = new PositionPath(pos2.x,pos2.y, null, Int.MaxValue, Int.MaxValue)
    var open_set = List(start_node)
    var closed_set = List[PositionPath]()
    var path = List[PositionPath]()
    var i = 0
    while(open_set.length > 0){
      i += 1
      open_set = open_set.sortWith(sortPos)
      var current_node = open_set.head
      open_set = open_set.tail
      closed_set = current_node :: closed_set
      if( current_node == end_node){
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
              var neighbour_node = open_set.find(p => p.x == (current_node.x+i) && p.y == (current_node.y+j)).getOrElse(
                new PositionPath(current_node.x + i, current_node.y+j,null,Int.MaxValue,Int.MaxValue)
              )
              var is_in_open_set = open_set.exists((p: PositionPath)=>p.x == (neighbour_node.x) && p.y == (neighbour_node.y))
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

    return null
  }
  // Cette fonction est l'heuristique utilisée pour le A* algorithme.
  // La version utilisée permet de prendre en compte les diagonales
  def heuristic_cost_estimate(nodeA: Position, nodeB: Position) : Int = {
    val deltaX = scala.math.abs(nodeA.x - nodeB.x);
    val deltaY = scala.math.abs(nodeA.y - nodeB.y);

    if (deltaX > deltaY)
      return 14 * deltaY + 10 * (deltaX - deltaY);
    return 14 * deltaX + 10 * (deltaY - deltaX);
  }

  // This function is used to create a new map
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

  // This function is used to know if there is a monster at a given position
  def searchMonster(monsterList: List[Monster], position:Position): Boolean = {
    monsterList match{
      case m::tl=> { 
        return(m.pos==position || searchMonster(tl,position))
      }
      case emptyList => { return(false)}
    }
  }
  // This function is used to know if a position is occupied by
  // The hero 
  // or a monster 
  // or an block environment that is blocking
  // It is used for collisions
  def occupied(pos: Position): Boolean = {
    return first_floor.getFloor()(pos.x)(pos.y).getBlocking || (pos==player.pos) || searchMonster(monsters,pos)
  }
  
  // This function is used to make monsters and items interact with the hero
  // It will move items from the ground the hero's inventory if it is not already full
  // and if the hero is "walking" on an item
  def processDecisions(){
    items.foreach((itm: Item)=> {
      itm.pickUp(this)
    }
    )
    // we remove items that are not on the ground anymore
    // from the list of items
    items = items.filter( itm =>{ 
      itm.on_the_ground == true })
    // We make all monsters do the action that their AI has decided
    monsters.foreach((m: Monster)=> {
        m.processDecision(this)
        }
      )
    // We filter dead monsters
    monsters = monsters.filter( m => m.state!=State.Dead)
    // This is temporary, but if all monsters are dead, we use it as a win condition
    // We spawn trophies, the hero has to pick one up, and use it to win
    monsters match{
      case m::tl => {}
      case emptyList => {
        if(!trophySpawned){
          placeItem(new Trophy(Origin))
        }
      }
    }
  }
}

