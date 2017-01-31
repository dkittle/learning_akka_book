package chapter3.actors

import java.net.URL
import javax.inject.{Inject, Named}

import akka.actor.{ActorLogging, ActorRef, Props, Status}
import chapter3.actors.FetcherActor.FetchUrl
import chapter3.actors.ParserActor.ParseItemXml

import scala.xml.NodeSeq

trait FetcherActorMBean {
  def getLastCalled: java.util.Date

  def getInvocations: Int

  def getLastUrl: String
}


class FetcherActor @Inject()(@Named("parser") parserRef: ActorRef) extends ActorWithJMX with FetcherActorMBean with ActorLogging {

  @volatile private[this] var lastUrl: String = ""

  override def receive = {
    case FetchUrl(x) => {
      log.info(s"Fetching $x")
      callMetrics
      grabXml(x).foreach(parserRef ! ParseItemXml(_))
    }
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }


  private def grabXml(url: String): NodeSeq = {
    lastUrl = url
    val xml = scala.xml.XML.load(new URL(url))
    xml \\ "item"
  }

  override def getMXTypeName: String = "RssActor"

  override def getLastUrl: String = lastUrl
}

object FetcherActor {

  case class FetchUrl(url: String)

  def props(): Props = {
    Props(classOf[FetcherActor])
  }

}

