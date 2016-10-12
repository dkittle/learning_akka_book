package chapter3.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, ActorSystem, Props, Status}
import chapter1.AkkaDb.StoreObject
import chapter3.actors.ParserActor.ParseItemXml
import models.Content

import scala.xml.NodeSeq

class ParserActor extends Actor with ActorLogging{

  implicit val system = ActorSystem()
  val dbRef: ActorSelection = system.actorSelection("/user/cache")

  override def receive = {
    case ParseItemXml(x) => storeContent(x)
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }


  private def storeContent(ns: NodeSeq): Unit ={
    val content = Content((ns \\ "link").text, (ns \\ "description").text)
    //log.info(s"link: ${content.url}, description: ${content.content}")
    dbRef ! StoreObject(java.net.URLEncoder.encode(content.url, "UTF-8"), content.content)
  }

}

object ParserActor {

  case class ParseItemXml(xml: NodeSeq)

  def props(): Props = {
    Props(classOf[ParserActor])
  }

}

