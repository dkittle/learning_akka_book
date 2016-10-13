package models

import play.api.libs.json.{Json, Reads, Writes}

case class UrlToRead(url: String)

case class Guid(guid: String)

object UrlToRead {

  implicit val urlReads: Reads[UrlToRead] = Json.reads[UrlToRead]
  implicit val urlWrites: Writes[UrlToRead] = Json.writes[UrlToRead]

}

object Guid {

  implicit val guidReads: Reads[Guid] = Json.reads[Guid]
  implicit val guidWrites: Writes[Guid] = Json.writes[Guid]

}
