package services

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import chapter2.StringReversingActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class StringReversingService {

  implicit val timeout = Timeout(5 seconds)

  def reverse(s: String): Future[(String, String)] = {
    val result = StringReversingService.stringReversingActor ? s
    result.mapTo[String].map(x => ("OK", x)).
      recover({ case e: Exception => ("KO", e.getMessage) })
  }

}

object StringReversingService {
  implicit val system = ActorSystem()

  lazy val stringReversingActor: ActorRef = system.actorOf(StringReversingActor.props())

}
