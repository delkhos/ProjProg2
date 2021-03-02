package rogue

/*
 * This is a submessage.
 * It is defined by a string and by a color.
 */
class SubMessage(str: String, col: String){
  val text = str
  val color = col
}

/*
 * A log message is a list of SubMessages.
 * We define it this way to print complex colored strings
 */
case class LogMessage(sub_messages: List[SubMessage]){
}

/*
 * The log is a finite list messages, to be printed to the screen
 */
object Log{ //register the messages to display on screen
  var messages = List[LogMessage]()
  def addLogMessage(msg: LogMessage){
    if(messages.length == 100){
      messages = messages.reverse.tail.reverse
    }
    messages = msg :: messages
  }
}
