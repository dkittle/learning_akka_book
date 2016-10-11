package chapter2

import java.net.URL

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Status}
import chapter2.FetcherActor.FetchUrl
import chapter2.ParserActor.ParseItemXml
import chapter2.StringReversingActor._

import scala.xml.{Elem, NodeSeq}

class FetcherActor extends Actor {

  implicit val system = ActorSystem()
  lazy val parserRef: ActorRef = system.actorOf(ParserActor.props())

  override def receive = {
    case FetchUrl(x) => grabXml(x).map (parserRef ! ParseItemXml(_))
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }


  private def grabXml(url: String): NodeSeq = {
    val xml = scala.xml.XML.load(new URL(url))
    (xml \\ "item")
  }

}

object FetcherActor {

  case class FetchUrl(url: String)
  def props(): Props = { Props(classOf[FetcherActor]) }

}

