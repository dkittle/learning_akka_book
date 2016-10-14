package chapter3.actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef, Status}

class FetcherActor extends Actor {

  override def receive = {
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }

}

object FetcherActor {

  case class FetchUrl(url: String)

}

