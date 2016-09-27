package controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc._
import services.StringReversingService

import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject() (stringReversalService: StringReversingService) extends Controller {

  def reverseString(s: String) = Action.async { implicit request =>
    stringReversalService.reverse(s).
      map { s =>
        Ok(Json.obj("status" -> s._1, "result" -> s._2))
    }
  }

}