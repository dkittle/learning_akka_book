package chapter1

import akka.actor.Actor

import scala.collection.mutable


class AkkaDb extends Actor {

  import AkkaDb._

  val map = mutable.Map.empty[String, Object]

  override def receive = {
    case StoreObject(k, v) =>
      map.put(k, v)
      sender() ! SuccessfulOperation(k)
    case GetObject(k) => sender() ! Result(k, map.get(k))
    case SetIfNotExists(k, v) =>
      if(map.get(k) == None) {
        map.put(k, v)
        sender() ! SuccessfulOperation(k)
      }
      else
        sender() ! FailedOperation(k)
    case Delete(k) =>
      if(map.get(k) == None)
        sender() ! FailedOperation(k)
      else {
        map.remove(k)
        sender() ! SuccessfulOperation(k)
      }
    case _ => sender() ! UnknownMessage
  }
}

object AkkaDb {

  val UnknownMessage = "received unknown message"

  case class StoreObject(k: String, v: Object)

  case class GetObject(k: String)

  case class SuccessfulOperation(k: String)

  case class Result(k: String, v: Option[Object])

  case class SetIfNotExists(k: String, v: Object)

  case class Delete(k: String)

  case class FailedOperation(k: String)

}