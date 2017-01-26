package services

import java.lang.management.ManagementFactory
import javax.management.{MBeanServer, ObjectName}

import chapter1.AkkaDb
import chapter3.actors.{FetcherActor, ParserActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment, Logger}

class ActorBinding(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    Logger.info("Starting Actors")
    bindActor[FetcherActor]("fetcher")
    bindActor[ParserActor]("parser")
    bindActor[AkkaDb]("cache")

//    val mbs: MBeanServer = ManagementFactory.getPlatformMBeanServer()
//    val mBeanName: ObjectName = new ObjectName(FetcherActor.getClass.getName + ":type=Actor")
//    mbs.registerMBean()
  }
}

