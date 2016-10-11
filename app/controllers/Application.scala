package controllers

import javax.inject.Inject

import models.UrlToRead
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import services.StringReversingService

import scala.concurrent.ExecutionContext.Implicits.global

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


  def readContentFromUrl = Action(BodyParsers.parse.json) { implicit rs =>
    val rssResult = rs.body.validate[UrlToRead]
    rssResult.fold(
      errors => {
        BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors)))
      },
      rssUrl => {
//        dao.save(employee.copy(json = rs.body.toString()))
        Ok(Json.obj("status" ->"OK", "message" -> ("RSS feed saved.") ))
      }
    )
  }

}