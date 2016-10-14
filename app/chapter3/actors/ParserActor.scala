package chapter3.actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef, Status}

class ParserActor @Inject()(@Named("cache") dbRef: ActorRef) extends Actor with ActorLogging {

  override def receive = {
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }

}

object ParserActor {

}

