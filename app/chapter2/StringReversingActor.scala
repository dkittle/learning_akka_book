package chapter2

import akka.actor.{Actor, Props}
import chapter2.StringReversingActor._

class StringReversingActor extends Actor {

  override def receive = {
    case _ => sender() ! CannotReverseException(ErrorMessage)
  }

}

object StringReversingActor {

  val ErrorMessage = "I cannot reverse that type"

  case class CannotReverseException(message: String) extends Exception(message)

  def props(): Props = { Props(classOf[StringReversingActor]) }

}