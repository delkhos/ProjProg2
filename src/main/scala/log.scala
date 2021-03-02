package rogue

class SubMessage(str: String, col: String){
  val text = str
  val color = col
}

case class LogMessage(sub_messages: List[SubMessage]){
}

object Log{ //register the messages to display on screen
  var messages = List[LogMessage]()
  def addLogMessage(msg: LogMessage){
    if(messages.length == 100){
      messages = messages.reverse.tail.reverse
    }
    messages = msg :: messages
  }
}
