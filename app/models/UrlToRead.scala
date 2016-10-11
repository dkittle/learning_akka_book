package models

import play.api.libs.json.{Json, Reads, Writes}

case class UrlToRead(url: String)

object UrlToRead {

  implicit val urlReads: Reads[UrlToRead] = Json.reads[UrlToRead]
  implicit val urlWrites: Writes[UrlToRead] = Json.writes[UrlToRead]

}
