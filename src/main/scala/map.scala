package rogue

import util.control.Breaks._

class Map(dim_arg: Dimension){
  var dim= dim_arg
  var floor = Array.ofDim[Environment](dim.width,dim.height)
  var hasBeenSeen = Array.ofDim[Int](dim.width,dim.height) 
  
  def getFloor(): Array[Array[Environment]] = {
    return floor
  }
 
  def getSeen(): Array[Array[Int]] = {
    return hasBeenSeen
  }
  
}

class MapAutomata(dim_arg: Dimension) extends Map(dim_arg){
  var floor2 = Array.ofDim[Environment](dim.width,dim.height)
  var floor3 = Array.ofDim[Int](dim.width,dim.height)
  var biomeMap = Array.ofDim[Biome](dim.width,dim.height)

  val r = scala.util.Random
  val initial_wall_chance = 40

  val birth = 4
  val death = 3

  def getFloor2(): Array[Array[Int]] = {
    return floor3
  }
  def getBiome(): Array[Array[Biome]] = {
    return biomeMap
  }
  def intToBiome(biomeID:Int): Biome = {
        biomeID match{
        case 0 => return( Neutral )
        case 1 => return( Field )
        case 2 => return ( Temple )
        case _ => return ( Cave )
        }
  }

  def initBiomeMap () {
    for (x<- 0 to  (dim.width-1)){
      for (y<- 0 to (dim.height-1)){
        biomeMap(x)(y) = Neutral
      }
    }
  }

  def oneGen():Boolean = {
    var continue = false
    for(x <- 0 to (dim.width-1) ){
      for(y <- 0 to (dim.height-1) ){
        var count1 = 0
        for(i <- -1 to 1){
          for(j <- -1 to 1){
            if(i==0 && j==0){
            }else if(x+i<0 || (x+i)>=dim.width  || (y+j)<0 || (y+j)>=dim.height){
              count1+=1
            }else if(floor(x+i)(y+j)==Granite){
              count1+=1
            }
          }
        }
        if(floor(x)(y)==Granite){
          if(count1 < death){
            floor2(x)(y) = Empty
            continue = true
          }else{
            floor2(x)(y) = Granite
          }
        }else{
          if(count1 > birth){
            floor2(x)(y) = Granite
            continue = true
          }else{
            floor2(x)(y) = Empty
          }
        }
      }
    }
    for(x <- 0 to (dim.width-1) ){
      for(y <- 0 to (dim.height-1) ){
          floor(x)(y) = floor2(x)(y)
      }
    }
    return continue
  }

  def generate() {
    for(x: Int <- 0 to (dim.width-1) ){
      for(y: Int <- 0 to (dim.height-1) ){
        if(r.nextInt(100)<initial_wall_chance){
          floor(x)(y)=Granite
        }else{
          floor(x)(y)=Empty
        }
      }
    }
    var k = 0
    while(oneGen() && k < 50 ){
      k+=1
    }
    for(y: Int <- 0 to (dim.height-1) ){
        floor(0)(y) = Granite
        floor(dim.width-1)(y) = Granite
    }
    for(x: Int <- 0 to (dim.width-1) ){
        floor(x)(0) = Granite
        floor(x)(dim.height-1) = Granite
    }
    for(x<- 0 to (dim.width-1) ){
      for(y: Int <- 0 to (dim.height-1) ){
          floor2(x)(y) = floor(x)(y)
      }
    }
  }
  //testing connexity
  //counting number of free spaces

  def pouring(x: Int, y: Int):Int = {
    //println("inner pouring at: "+x+"  "+y+"  "+floor(x)(y))
    if(floor3(x)(y)!=0 ){
      return 0;
    }else{
      floor3(x)(y) = 1
      return 1 + pouring(x+1,y)+ pouring(x-1,y) + pouring(x,y+1) + pouring(x,y-1) 
    }
  }

  def testConnexity(free: Int):Boolean = {
    for(x <- 0 to (dim.width-1) ){
      for(y <- 0 to (dim.height-1) ){
        floor3(x)(y) = if (floor(x)(y)==Granite) 1 else 0
      }
    }
    // using bucket pour method
    var i = 1
    var stop = false

    while(i <= (dim.width-2) && !stop ){
      var j = 1
      while(j <= (dim.height-2)  && !stop){ 
        //println("pouring at: "+i+"  "+j)
        val pour_value = pouring(i,j)
        if(pour_value!=free && pour_value!=0){
          //println("non connexe avec le noeud : "+i+"  "+j)
          stop = true
        }
        j += 1
      }
      i += 1
    }
    if(stop == false){
      println("connexe")
    }
    return !stop
  }

  def pouringRoom(x: Int, y: Int, n: Int, b: Biome):Int = {
    //println("inner pouring at: "+x+"  "+y+"  "+floor(x)(y))
    if(floor3(x)(y)!=0 ){
      return 0;
    }else{
      if (biomeMap(x)(y) == Neutral){
        floor3(x)(y) = n
        biomeMap(x)(y) = b
        return 1 + pouringRoom(x+1,y,n,b)+ pouringRoom(x-1,y,n,b) + pouringRoom(x,y+1,n,b) + pouringRoom(x,y-1,n,b) 
      }else{
        floor3(x)(y) = n
        return 1 + pouringRoom(x+1,y,n,b)+ pouringRoom(x-1,y,n,b) + pouringRoom(x,y+1,n,b) + pouringRoom(x,y-1,n,b) 
      }
    }  
  }

  def getRooms(free: Int, room_number: Int, biome: Biome):Boolean = {
    // using bucket pour method
    var i = 1
    var stop = false
    while(i <= (dim.width-2) && !stop ){
      var j = 1
      while(j <= (dim.height-2)  && !stop){ 
        val pour_value = pouringRoom(i,j,room_number,biome)
        if(pour_value!=free && pour_value!=0){
          //println("non connexe avec le noeud : "+i+"  "+j)
          stop = true
        }
        j += 1
      }
      i += 1
    }
    return !stop
  }
  def getAllRooms():Int = {
    for(x <- 0 to (dim.width-1) ){
      for(y <- 0 to (dim.height-1) ){
        floor3(x)(y) = if (floor(x)(y)==Granite) 1 else 0
        //floor2(x)(y) = floor(x)(y)
      }
    }
    var biome: Biome = intToBiome(r.nextInt(4))
    var free = 0
    for(x <- 0 to (dim.width-1) ){
      for(y <- 0 to (dim.height-1) ){ 
        if(floor3(x)(y)!=1){
          free += 1
        }
      }
    }
    var n = 1
    while(!getRooms(free,n,biome)){
      n += 1
      biome = intToBiome(r.nextInt(4))
      println("the chosen biome is "+ biome)
      free = 0
      for(x <- 0 to (dim.width-1) ){
        for(y <- 0 to (dim.height-1) ){ 
          if(floor3(x)(y)==1){
            free += 1
          }
        }
      }
    }
    return n
  }

  def carveOneTunnel(){
    var tx1 = 0 
    var ty1 = 0 
    var d1 = 1000 
    var tx2 = 0 
    var ty2 = 0
    var alongX = true
    for(x1 <- 0 to (dim.width-1) ){
      for(y1 <- 0 to (dim.height-1) ){
        if(floor3(x1)(y1) == 1 && floor(x1)(y1)==Empty){
          for(x2 <- 0 to (dim.width-1) ){
            for(y2 <- 0 to (dim.height-1) ){
              if((x1!=x2 || y1!=y2) && (x1==x2 || y1==y2) && floor3(x2)(y2)>1)
              {
                var d = 0
                var alX = true
                if(x1==x2){
                  d = scala.math.abs(y1-y2)
                  alX = false
                }else{
                  d = scala.math.abs(x1-x2)
                  alX = true
                }
                if(d < d1){
                  tx1 = x1 
                  ty1 = y1 
                  d1 = d 
                  tx2 = x2 
                  ty2 = y2
                  alongX = alX
                }
              }
            }
          }
        }
      }
    }
    if(tx1==0 && ty1==0){
      return
    }
    println("chosen ones : "+tx1+":"+ty1+" "+tx2+":"+ty2)
    //on a trouvé les deux points les plus proches entre la salle 1 et une autre salle
    if(alongX == true){
      if(tx1 <= tx2){
        for(i <- tx1 to tx2){
          floor(i)(ty1) = EmptyTemple
          biomeMap(i)(ty1) = Temple
          floor3(i)(ty1) = 0
        }
      }else{
        for(i <- tx2 to tx1){
          floor(i)(ty1) = EmptyTemple
          biomeMap(i)(ty1) = Temple
          floor3(i)(ty1) = 0
        }
      }
    }else{
      if(ty1 <= ty2){
        for(j <- ty1 to ty2){
          floor(tx1)(j) = EmptyTemple
          biomeMap(tx1)(j) = Temple
          floor3(tx1)(j) = 0
        }
      }else{
        for(j <- ty2 to ty1){
          floor(tx1)(j) = EmptyTemple
          biomeMap(tx1)(j) = Temple
          floor3(tx1)(j) = 0
        }
      }
    }
  }

  def generateConnexRandom(){
    generate()
    var free = 0
    for(x <- 0 to (dim.width-1) ){
      for(y <- 0 to (dim.height-1) ){ 
        if(floor(x)(y)!=Granite){
          free += 1
        }
      }
    }
    while(!testConnexity(free)){
      generate()
    }
  }

  def generateConnexByCarving():Int={
    generate()
    initBiomeMap()
    val room_count=getAllRooms()
    println( "room_count is " + room_count )
    while(getAllRooms()>1){
      carveOneTunnel()
    }
    return room_count
  }

  
  def applyBiomes(){
    for (x: Int <- 0 to (dim.width-1)){
      for (y: Int <- 0 to (dim.height-1)){
        if (floor(x)(y) == Empty){
          floor(x)(y) = biomeMap(x)(y) match{
            case Field => EmptyField
            case Cave => EmptyCave
            case Neutral => Empty
            case Temple => EmptyTemple
          }
        }
      }
    }
  }

  
  generateConnexByCarving()
  applyBiomes()

}
/*
class MapPolygon(dim.width: Int, dim.height: Int, sides:Int, radius: Int, rotation: Double) extends Map(dim.width,height){
  val rot = rotation*(scala.math.Pi / 180.0)

  val rchange: Double = (scala.math.Pi * 2.0) / sides
  var r: Double = 0.0
  while(r < scala.math.Pi*2)
  {
    val p1_x:Double = radius + scala.math.cos(r + rot) * radius;
    val p1_y:Double = radius + scala.math.sin(r + rot) * radius;

    // define second point (rotated 1 iteration further).
    val p2_x:Double = radius + scala.math.cos(r + rot + rchange) * radius;
    val p2_y:Double = radius + scala.math.sin(r + rot + rchange) * radius;

    val len:Double = scala.math.sqrt(scala.math.pow(p2_x - p1_x, 2) + scala.math.pow(p2_y - p1_y, 2));
    var i:Double = 0.0
    while( i < 1)
    {
        val place_x:Int = scala.math.round((1 - i) * p1_x + i * p2_x).toInt;
        val place_y:Int = scala.math.round((1 - i) * p1_y + i * p2_y).toInt;

        floor(place_y)(place_x) = 1;

        i+= 1.0/len
    }

    r += rchange

  }
}
*/




