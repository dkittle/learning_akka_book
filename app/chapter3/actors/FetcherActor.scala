package chapter3.actors

import java.net.URL
import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef, Props, Status}
import chapter3.actors.FetcherActor.FetchUrl
import chapter3.actors.ParserActor.ParseItemXml

import scala.xml.NodeSeq

class FetcherActor @Inject()(@Named("parser") parserRef: ActorRef) extends Actor {

  override def receive = {
    case FetchUrl(x) => grabXml(x).foreach(parserRef ! ParseItemXml(_))
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }


  private def grabXml(url: String): NodeSeq = {
    val xml = scala.xml.XML.load(new URL(url))
    xml \\ "item"
  }

}

object FetcherActor {

  case class FetchUrl(url: String)

  def props(): Props = {
    Props(classOf[FetcherActor])
  }

}

