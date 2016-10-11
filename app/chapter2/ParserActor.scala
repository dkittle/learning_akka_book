package chapter2

import akka.actor.{Actor, Props, Status}
import chapter2.ParserActor.ParseItemXml
import models.Content

import scala.xml.NodeSeq

class ParserActor extends Actor {

  override def receive = {
    case ParseItemXml(x) => storeContent(x)
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }


  private def storeContent(ns: NodeSeq): Unit ={
    val content = Content((ns \\ "link").text, (ns \\ "description").text)
  }

}

object ParserActor {

  case class ParseItemXml(xml: NodeSeq)

  def props(): Props = {
    Props(classOf[ParserActor])
  }

}

