package controllers

import java.util.concurrent.TimeoutException
import javax.inject.Inject

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask

import scala.concurrent.duration._
import akka.util.Timeout
import play.api.libs.json.Json
import play.api.mvc._
import services.StringReversingService

import scala.concurrent.ExecutionContext.Implicits.global
import chapter1.AkkaDb

import scala.concurrent.{Await, Future}



class Application @Inject() (stringReversingService: StringReversingService) extends Controller {
  implicit val system = ActorSystem()
  val akkaDb = system.actorOf(Props[AkkaDb])


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

  def fetchContentFromUrl = Action(parse.json) { request =>
    (request.body \ "url").asOpt[String].map{ url =>

      val body = de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(new java.net.URL(url))
      akkaDb ! AkkaDb.StoreObject(url, body)
      Ok(Json.obj("status" -> s"OK, let's store $url => $body"))

    }.getOrElse{
      BadRequest("Missing parameter [url]")
    }
  }

  def getStoredContent(url: String) = Action { request =>
//    (request.body \ "url").asOpt[String].map { url =>

      implicit val timeout = Timeout(2 seconds)
      val waitTimeout = 2 seconds
      //      case class Result(k: String, v: Option[Object])
      val response = (akkaDb ? AkkaDb.GetObject(url))
      val res = Await.result(response, waitTimeout).asInstanceOf[AkkaDb.Result]

      res match {
        case AkkaDb.Result(key, Some(content: String)) => Ok(Json.obj("content" -> content))
        case AkkaDb.Result(key, None) => NotFound
      }
    }

//      .getOrElse{
//      BadRequest("Missing parameter [url]")
//    }
//  }
}