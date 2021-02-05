package rogue

import java.awt.Color

class SubSprite(char_code: Int, color: String){
  def getColor(): String = {
    return color
  }
  def getCharCode(): Int = {  
    return char_code
  }
}
class Sprite(elements: Array[SubSprite], bg_color: Color){
  def getBgColor(): Color = {
    return bg_color
  }
  def getElements(): Array[SubSprite] = {  
    return elements
  }
}
