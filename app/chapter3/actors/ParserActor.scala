package chapter3.actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Status}
import chapter1.AkkaDb.StoreObject
import chapter3.actors.ParserActor.ParseItemXml
import models.Content

import scala.xml.NodeSeq

class ParserActor @Inject()(@Named("cache") dbRef: ActorRef) extends Actor with ActorLogging {

  override def receive = {
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }

}

object ParserActor {

}

