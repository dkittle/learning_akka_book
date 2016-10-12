package services

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef}

object FetcherActor {

}

class FetcherActor @Inject()(@Named("parser") parser: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case x => parser ! x.toString
  }

}