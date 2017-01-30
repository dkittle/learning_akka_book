package chapter3.actors

import java.lang.management.ManagementFactory
import java.util.Date
import javax.management.ObjectName

import akka.actor.Actor

trait ActorWithJMX extends Actor {

  @volatile var lastCalled: java.util.Date = new java.util.Date()
  @volatile var invocations: Int = 0

  val objName = new ObjectName("infomart", {
    import scala.collection.JavaConverters._
    new java.util.Hashtable(
      Map(
        "name" -> self.path.toStringWithoutAddress,
        "type" -> getMXTypeName
      ).asJava
    )
  })

  def callMetrics = {
    lastCalled = new java.util.Date()
    invocations += 1
  }

  def getMXTypeName : String

  def getLastCalled: Date = lastCalled

  def getInvocations: Int = invocations

  override def preStart() = ManagementFactory.getPlatformMBeanServer().registerMBean(this, objName)

  override def postStop() = ManagementFactory.getPlatformMBeanServer().unregisterMBean(objName)
}
