package chapter2

import akka.actor.{Actor, Props, Status}
import chapter2.StringReversingActor._

class StringReversingActor extends Actor {

  override def receive = {
    case s: String => sender() ! s.reverse
    case _ => sender() ! Status.Failure(CannotReverseException())
  }

}

object StringReversingActor {

  case class CannotReverseException() extends Exception("Cannot reverse that type")

  def props(): Props = { Props(classOf[StringReversingActor]) }

}

