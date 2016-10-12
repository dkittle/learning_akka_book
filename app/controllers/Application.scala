package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorRef, ActorSystem, Props}
import chapter1.AkkaDb
import chapter1.AkkaDb.GetObject
import chapter3.actors.FetcherActor.FetchUrl
import chapter3.actors.FetcherActor
import models.UrlToRead
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import services.StringReversingService
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Application @Inject() (stringReversingService: StringReversingService) extends Controller {

  def reverseString(s: String) = Action.async { implicit request =>
    stringReversingService.reverse(s).
      map (s => Ok(Json.obj("status" -> "OK", "result" -> s))).
        recover {
          case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
        }
  }

  def reverseAll(phrase: Seq[String]) = Action.async {
    stringReversingService.reverseAll(phrase).
      map (s => Ok(Json.obj("status" -> "OK", "result" -> s.mkString((","))))).
        recover {
          case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
        }
  }


  implicit val system = ActorSystem()

  lazy val fetcherRef: ActorRef = system.actorOf(FetcherActor.props())
  val dbRef: ActorRef = system.actorOf(Props[AkkaDb], "cache")

  def readContentFromUrl = Action(BodyParsers.parse.json) { implicit rs =>
    val rssResult = rs.body.validate[UrlToRead]
    rssResult.fold(
      errors => {
        BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors)))
      },
      rssUrl => {
        fetcherRef ! FetchUrl(rssUrl.url)
        Ok(Json.obj("status" ->"OK", "message" -> ("RSS feed saved.") ))
      }
    )
  }

  def retrieveContentByUrl(url : String) = Action.async {
    implicit val timeout = Timeout(5 seconds)
    val content: Future[AkkaDb.Result] = (dbRef ? GetObject(url)).mapTo[AkkaDb.Result]
    content.map { case AkkaDb.Result(_, Some(v)) => Ok(Json.obj("status" -> "OK", "result" -> v.toString)) }.
      recover {
        case e: Exception => Ok(Json.obj("status" -> "KO", "result" -> e.getMessage))
      }
  }

}