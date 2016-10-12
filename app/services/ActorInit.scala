package services

import java.util.UUID
import javax.inject.Inject

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props}
import chapter1.AkkaDb
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment, Logger, Play}

class ActorInit(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    Logger.info("Starting Actors")
    bindActor[FetcherActor]("fetcher")
    bindActor[ParserActor]("parser")
    bindActor[AkkaDb]("akkaDb")
  }
}

