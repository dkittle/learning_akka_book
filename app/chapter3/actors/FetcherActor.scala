package chapter3.actors

import java.net.URL
import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef, Props, Status}
import chapter3.actors.FetcherActor.FetchUrl
import chapter3.actors.ParserActor.ParseItemXml

import scala.xml.NodeSeq

class FetcherActor @Inject()(@Named("parser") parserRef: ActorRef) extends Actor {

  override def receive = {
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }

}

object FetcherActor {

  case class FetchUrl(url: String)

}

