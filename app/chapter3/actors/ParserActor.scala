package chapter3.actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Status}
import chapter1.DbActor.{StoreObject, SuccessfulOperation}
import chapter3.actors.ParserActor.ParseItemXml
import models.Content

import scala.xml.NodeSeq

trait ParserActorMBean {
  def getLastCalled: java.util.Date

  def getInvocations: Int

  def getLastGuid: String
}

class ParserActor @Inject()(@Named("cache") dbRef: ActorRef) extends ActorWithJMX with ParserActorMBean with ActorLogging {

  @volatile private[this] var lastGuid: String = ""

  override def receive = {
    case ParseItemXml(x) =>
      log.info(s"Receiving $x")
      callMetrics
      storeContent(x)
    case SuccessfulOperation(k) => log.info(s"Key $k stored")
    case y =>
      log.info(s"Invalid Message $y from ${sender().path}")
      sender() ! Status.Failure(new Exception("invalid message"))
  }

  override def getMXTypeName: String = "RssActor"

  override def getLastGuid: String = lastGuid

  private def storeContent(ns: NodeSeq): Unit = {
    val content = Content((ns \\ "guid").text, (ns \\ "description").text)
    lastGuid = content.guid
    log.info(s"Parsing guid ${content.guid}")
    dbRef ! StoreObject(content.guid, content.content)
  }

}

object ParserActor {

  case class ParseItemXml(xml: NodeSeq)

  def props(): Props = {
    Props(classOf[ParserActor])
  }

}

