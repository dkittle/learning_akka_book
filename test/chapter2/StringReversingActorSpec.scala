package chapter2

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import chapter2.StringReversingActor._

class StringReversingActorSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FeatureSpecLike with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val actorRef = TestActorRef(StringReversingActor.props())
  val Timeout = 2 seconds

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  feature("Test the string reversing actor") {

    scenario("Expect an error if I sent the string reversing actor the wrong type") {
      Given("a string reversing actor actor")
      When("an invalid message type is sent")
      actorRef ! 42
      Then("the actor should send an error message back")
      expectMsg(Timeout, CannotReverseException(ErrorMessage))
    }


  }

}
