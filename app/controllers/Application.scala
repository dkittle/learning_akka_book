package controllers

import javax.inject.Inject

import play.api.libs.json.Json
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


}