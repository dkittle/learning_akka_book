package chapter1

import scala.concurrent.duration._

import chapter1.AkkaDb.StoreObject
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest._

class AkkaDbSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FeatureSpecLike with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val actorRef = TestActorRef[AkkaDb]
  val akkaDb = actorRef.underlyingActor

  val Key = "key"
  val UnknownKey = "foo"
  val OldValue = "old-value"
  val Value = "value"
  val Timeout = 2 seconds

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  feature("Exercise the storage and retrieval features of the DB actor") {
    scenario("The DB actor sends a message back when sent an unknown message") {
      Given("a DB actor in any state")
      akkaDb.map.get(Key) should be (None)
      When("an unknkown message is sent")
      actorRef ! "foo"
      Then("the DB actor should send a specific error message back")
      expectMsg(Timeout, AkkaDb.UnknownMessage)
    }

    scenario("The DB actor can store values") {
      Given("DB actor does not have a specific value")
      akkaDb.map.get(Key) should be (None)
      When("a value is stored")
      actorRef ! StoreObject(Key, Value)
      Then("the DB actor should have the stored value")
      akkaDb.map.get(Key) should equal (Some(Value))
    }

    scenario("The DB actor will return None if it doesn't have a value") {
      Given("DB actor does not have a specific key")
      akkaDb.map.get(UnknownKey) should be (None)
      When("a value is with a different key is stored")
      actorRef ! StoreObject(Key, Value)
      Then("the DB actor should have still have no value for the original key")
      akkaDb.map.get(UnknownKey) should equal (None)
    }

    scenario("The DB actor will update values") {
      Given("DB actor has a specific value")
      actorRef ! StoreObject(Key, OldValue)
      akkaDb.map.get(Key) should be (Some(OldValue))
      When("a value is updated")
      actorRef ! StoreObject(Key, Value)
      Then("the DB actor should have the new value")
      akkaDb.map.get(Key) should equal (Some(Value))
    }

  }

}

