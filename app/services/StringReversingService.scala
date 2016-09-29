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

  def reverse(s: String): Future[String] = {
    (StringReversingService.stringReversingActor ? s).mapTo[String]
  }

  def reverseAll(xs: Seq[String]): Future[Seq[String]] = {
    Future.sequence(xs.map(x => (StringReversingService.stringReversingActor ? x).mapTo[String]))
  }

}

object StringReversingService {
  implicit val system = ActorSystem()

  lazy val stringReversingActor: ActorRef = system.actorOf(StringReversingActor.props())

}
