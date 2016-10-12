package controllers

import javax.inject.{Inject, Named}

import akka.actor.ActorRef
import de.l3s.boilerpipe.extractors.ArticleExtractor
import play.api.libs.json.{JsError, JsPath, Json, Writes}
import play.api.mvc._
import services.StringReversingService
import akka.pattern.ask
import play.api.libs.json.Json._

import scala.language.postfixOps
import play.api.libs.functional.syntax._

import scala.concurrent.duration._
import akka.util.Timeout
import chapter1.AkkaDb.{GetAllKeys, GetObject, SuccessfulOperation}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Application @Inject()(stringReversingService: StringReversingService, @Named("fetcher") fetcher: ActorRef, @Named("akkaDb") akkaDb: ActorRef) extends Controller {

  implicit val duration: Timeout = 60.seconds

  case class RssUrl(url: String)

  case class RssResult(url: String, content: String)

  implicit val rssUrlImplicitReads = Json.reads[RssUrl]
  implicit val rssUrlImplicitWrites = Json.writes[RssUrl]
  implicit val rssResultImplicitWrites = Json.writes[RssResult]

  def reverseString(s: String) = Action.async { implicit request =>
    stringReversingService.reverse(s).
      map(s => Ok(Json.obj("status" -> "OK", "result" -> s))).
      recover {
        case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
      }
  }

  def reverseAll(phrase: Seq[String]) = Action.async {
    stringReversingService.reverseAll(phrase).
      map(s => Ok(Json.obj("status" -> "OK", "result" -> s.mkString((","))))).
      recover {
        case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
      }
  }

  def content() = Action(BodyParsers.parse.json) { request =>
    val rssUrl = request.body.validate[RssUrl]
    rssUrl.fold(
      errors => {
        BadRequest
      },
      url => {
        fetcher ! url.url
        Ok
      }
    )
  }

  def retrieveContent() = Action.async(BodyParsers.parse.json) { request =>
    val rssUrl = request.body.validate[RssUrl]

    rssUrl.fold(
      errors => {
        Future(BadRequest)
      },
      url => {
        (akkaDb ? GetObject(url.url)).map {
          case chapter1.AkkaDb.Result(_, Some(y)) => Ok(toJson(RssResult(url.url, y.toString)))
          case _ => NotFound
        }
      }
    )
  }

  def retrieveUrls() = Action.async {
    (akkaDb ? GetAllKeys).map {
      case x: Seq[String] => Ok(toJson(x.map(RssUrl(_))))
      case _ => NotFound
    }
  }
}
