package rogue

import java.awt.Color

// A SubSprite is an element of our tileset, and a color
// for this element
class SubSprite(char_code: Int, color: String){
  def getColor(): String = {
    return color
  }
  def getCharCode(): Int = {  
    return char_code
  }
}

// A sprite is a superposition of subsprites,
// and a background color
class Sprite(elements: Array[SubSprite], bg_color: Color){
  def getBgColor(): Color = {
    return bg_color
  }
  def getElements(): Array[SubSprite] = {  
    return elements
  }
}
