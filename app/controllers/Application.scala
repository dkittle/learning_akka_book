package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import chapter1.AkkaDb
import chapter1.AkkaDb.{GetKeys, GetObject}
import chapter3.actors.FetcherActor.FetchUrl
import models.{Guid, UrlToRead}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import services.StringReversingService

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class Application @Inject()(stringReversingService: StringReversingService,
                            @Named("fetcher") fetcherRef: ActorRef,
                            @Named("cache") dbRef: ActorRef)
                           (implicit ec: ExecutionContext)
  extends Controller {

  implicit val timeout = Timeout(5 seconds)

  /**
    * Chapter 2 endpoints
    */
  def reverseString(s: String) = Action.async { implicit request =>
    stringReversingService.reverse(s).
      map(s => Ok(Json.obj("status" -> "OK", "result" -> s))).
      recover {
        case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
      }
  }

  def reverseAll(phrase: Seq[String]) = Action.async {
    stringReversingService.reverseAll(phrase).
      map(s => Ok(Json.obj("status" -> "OK", "result" -> s.mkString(",")))).
      recover {
        case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
      }
  }

  /**
    * Chapter 3 exercise endpoints
    */
  def readContentFromUrl = Action(BodyParsers.parse.json) { implicit rs =>
    val rssResult = rs.body.validate[UrlToRead]
    rssResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors)))
      },
      rssUrl => {
        fetcherRef ! FetchUrl(rssUrl.url)
        Ok(Json.obj("status" -> "OK", "message" -> "RSS feed saved."))
      }
    )
  }

  def retrieveContentByGuid(guid: String) = Action.async {
    (dbRef ? GetObject(guid)).map {
      case AkkaDb.Result(_, Some(v: String)) => Ok(Json.obj("status" -> "OK", "result" -> v))
      case AkkaDb.Result(_, None) => NotFound
      case _ => BadRequest
    }.recover {
      case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
    }
  }

  def retrieveGuids() = Action.async {
    (dbRef ? GetKeys).mapTo[Seq[String]].map {
      s => Ok(Json.obj("status" -> "OK", "result" -> s.map(Guid(_)).toString))
    }.recover {
      case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
    }
  }

}