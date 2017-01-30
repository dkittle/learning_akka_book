package chapter1

import akka.actor.Actor

import scala.collection.mutable
import chapter3.actors.ActorWithJMX

trait DbActorMBean {
  def getLastCalled: java.util.Date

  def getInvocations: Int

  def getNumberEntries: Int
}

class DbActor extends ActorWithJMX with DbActorMBean {

  import DbActor._

  val cache = mutable.Map.empty[String, Object]

  override def receive = {
    case StoreObject(k, v) =>
      callMetrics
      cache.put(k, v)
      sender() ! SuccessfulOperation(k)
    case GetObject(k) =>
      callMetrics
      sender() ! Result(k, cache.get(k))
    case SetIfNotExists(k, v) =>
      callMetrics
      if(cache.get(k).isEmpty) {
        cache.put(k, v)
        sender() ! SuccessfulOperation(k)
      }
      else
        sender() ! FailedOperation(k)
    case Delete(k) =>
      callMetrics
      if(cache.get(k).isEmpty)
        sender() ! FailedOperation(k)
      else {
        cache.remove(k)
        sender() ! SuccessfulOperation(k)
      }
    case GetKeys =>
      callMetrics
      sender() ! cache.keys.toSeq
    case _ => sender() ! UnknownMessage
  }

  override def getNumberEntries: Int = cache.size

  override def getMXTypeName: String = "RssActor"
}

object DbActor {

  val UnknownMessage = "received unknown message"

  case class StoreObject(k: String, v: Object)

  case class GetObject(k: String)

  case class SuccessfulOperation(k: String)

  case class Result(k: String, v: Option[Object])

  case class SetIfNotExists(k: String, v: Object)

  case class Delete(k: String)

  case class FailedOperation(k: String)

  case object GetKeys

}

