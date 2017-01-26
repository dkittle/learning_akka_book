package chapter3.actors

import java.beans.ConstructorProperties
import java.lang.management.ManagementFactory
import java.net.URL
import javax.inject.{Inject, Named}
import javax.management.ObjectName

import akka.actor.{Actor, ActorRef, Props, Status}
import chapter3.actors.FetcherActor.FetchUrl
import chapter3.actors.ParserActor.ParseItemXml

import scala.beans.BeanProperty
import scala.xml.NodeSeq

trait FetcherActorMBean {
  def getFetcherStats: FetcherStats
  def getFetcherStatsMXView: FetcherStatsMXView
}

trait ActorWithJMX extends Actor {

  val objName = new ObjectName("infomart", {
    import scala.collection.JavaConverters._
    new java.util.Hashtable(
      Map(
        "name" -> self.path.toStringWithoutAddress,
        "type" -> getMXTypeName
      ).asJava
    )
  })

  def getMXTypeName : String

  override def preStart() = ManagementFactory.getPlatformMBeanServer().registerMBean(this, objName)

  override def postStop() = ManagementFactory.getPlatformMBeanServer().unregisterMBean(objName)
}


case class FetcherStats(
                       lastCalled: java.util.Date,
                       invocations: Int,
                       lastUrl: FetchUrl
                       )

class FetcherStatsMXView @ConstructorProperties(Array("lastCalled", "invocations", "lastUrl")) (@BeanProperty val lastCalled: java.util.Date, @BeanProperty val invocations: Int, @BeanProperty val lastUrl: String)


object FetcherStatsMXView {
  def apply(fetchStats: FetcherStats): FetcherStatsMXView = {
    new FetcherStatsMXView(fetchStats.lastCalled, fetchStats.invocations, fetchStats.lastUrl.url)
  }
}

class FetcherActor @Inject()(@Named("parser") parserRef: ActorRef) extends ActorWithJMX with FetcherActorMBean {

  @volatile private[this] var fetcherStats: Option[FetcherStats] = None

  override def receive = {
    case FetchUrl(x) => {
      fetcherStats = Some(FetcherStats(new java.util.Date(), 1, FetchUrl(x)))
      grabXml(x).foreach(parserRef ! ParseItemXml(_))
    }
    case _ => sender() ! Status.Failure(new Exception("invalid message"))
  }


  private def grabXml(url: String): NodeSeq = {
    val xml = scala.xml.XML.load(new URL(url))
    xml \\ "item"
  }

  override def getFetcherStats = fetcherStats.orNull

  override def getFetcherStatsMXView: FetcherStatsMXView = fetcherStats.map(FetcherStatsMXView(_)).orNull

  override def getMXTypeName: String = "RSSActor"
}

object FetcherActor {

  case class FetchUrl(url: String)

  def props(): Props = {
    Props(classOf[FetcherActor])
  }

}

