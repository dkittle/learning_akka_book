package services

import chapter1.DbActor
import chapter3.actors.{FetcherActor, ParserActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment, Logger}

class ActorBinding(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    Logger.debug(s"Starting Actors")
    bindActor[FetcherActor]("fetcher")
    bindActor[ParserActor]("parser")
    bindActor[DbActor]("cache")
  }
}

