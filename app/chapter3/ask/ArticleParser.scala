package chapter3.ask

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import chapter1.AkkaDb.{GetObject, StoreObject}
import chapter3.{ArticleBody, HttpResponse, ParseArticle, ParseHtmlArticle}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
    case ParseArticle(uri) =>
      val senderRef = sender()
      val cacheResult = cacheActor ? GetObject(uri)
      val result = cacheResult.recoverWith {
        case _: Exception =>
          val fRawResult = httpClientActor ? uri
          fRawResult flatMap {
            case HttpResponse(rawArticle) =>
              articleParserActor ? ParseHtmlArticle(uri, rawArticle)
            case x =>
              Future.failed(new Exception("unknown response"))
          }
      }
      result onComplete {
        //could use Pipe (covered later)
        case scala.util.Success(x: String) =>
          log.info("cached result!")
          senderRef ! x //cached result
        case scala.util.Success(x: ArticleBody) =>
          cacheActor ! StoreObject(uri, x.body)
          senderRef ! x
        case scala.util.Failure(t) =>
          senderRef ! akka.actor.Status.Failure(t)
        case x =>
          log.info(s"unknown message! $x")
      }
  }
}