package chapter2

import akka.actor.{Actor, Props, Status}

class PongActor(response: String) extends Actor {

  override def receive = {
    case "Ping" => sender() ! response
    case "Pong" => sender() ! "Deja Vu"
    case _ => sender() ! Status.Failure(new Exception("unknown message"))
  }

}

object PongActor {

  def props(response: String): Props = {
    Props(classOf[PongActor], response)
  }
}
