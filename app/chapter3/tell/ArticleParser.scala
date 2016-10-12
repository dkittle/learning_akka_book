package chapter3.tell

import akka.actor.Status.Failure

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import chapter1.AkkaDb.{GetObject, StoreObject}
import chapter3.{ArticleBody, HttpResponse, ParseArticle, ParseHtmlArticle}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.TimeoutException

class ArticleParser(cacheActorPath: String,
                    httpClientActorPath: String,
                    acticleParserActorPath: String,
                    implicit val timeout: Timeout
                   )
  extends Actor with ActorLogging {
  val cacheActor = context.actorSelection(cacheActorPath)
  val httpClientActor = context.actorSelection(httpClientActorPath)
  val articleParserActor = context.actorSelection(acticleParserActorPath)

  override def receive: Receive = {
    case msg@ParseArticle(uri) =>
      val extraActor = buildExtraActor(sender(), uri)
      cacheActor.tell(GetObject(uri), extraActor)

      httpClientActor.tell("test", extraActor)
      context.system.scheduler.scheduleOnce(3 seconds, extraActor, "timeout")
  }

  private def buildExtraActor(senderRef: ActorRef, uri: String): ActorRef = {
    context.actorOf(Props(new Actor {
      override def receive = {
        case "timeout" => //if we get timeout, then fail
          senderRef ! Failure(new TimeoutException("timeout!"))
          context.stop(self)
        case HttpResponse(body) => //If we get the http response first, we pass it to be parsed.
        articleParserActor ! ParseHtmlArticle(uri, body)
        case body: String => //If we get the cache response first, we handle it and shut down.
            //The cache response will come back before the HTTP response so we never parse in this case.
        senderRef ! body
        context.stop(self)
        case ArticleBody(uri, body) => //If we get the parsed article we just parsed it
          cacheActor ! StoreObject(uri, body) //Cache it as we just parsed it
          senderRef ! body
        context.stop(self)
        case t => //We can get a cache miss
          log.info(s"ignoring msg: ${t.getClass}")
      }
    }))
  }
}