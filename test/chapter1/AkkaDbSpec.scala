package chapter1

import scala.concurrent.duration._
import chapter1.DbActor._
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest._

class AkkaDbSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FeatureSpecLike with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val actorRef = TestActorRef[DbActor]
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
      akkaDb.cache.get(Key) should be (None)
      When("an unknkown message is sent")
      actorRef ! "foo"
      Then("the DB actor should send a specific error message back")
      expectMsg(Timeout, DbActor.UnknownMessage)
    }

    scenario("The DB actor can store values") {
      Given("DB actor does not have a specific value")
      akkaDb.cache.get(Key) should be (None)
      When("a value is stored")
      actorRef ! StoreObject(Key, Value)
      expectMsg(SuccessfulOperation(Key))
      Then("the DB actor should have the stored value")
      akkaDb.cache.get(Key) should equal (Some(Value))
    }

    scenario("The DB actor will return None if it doesn't have a value") {
      Given("DB actor does not have a specific key")
      akkaDb.cache.get(UnknownKey) should be (None)
      When("a value is with a different key is stored")
      actorRef ! StoreObject(Key, Value)
      expectMsg(SuccessfulOperation(Key))
      Then("the DB actor should have still have no value for the original key")
      akkaDb.cache.get(UnknownKey) should equal (None)
    }

    scenario("The DB actor will update values") {
      Given("DB actor has a specific value")
      actorRef ! StoreObject(Key, OldValue)
      expectMsg(SuccessfulOperation(Key))
      When("a value is updated")
      actorRef ! StoreObject(Key, Value)
      expectMsg(SuccessfulOperation(Key))
      Then("the DB actor should have the new value")
      akkaDb.cache.get(Key) should equal (Some(Value))
    }

    scenario("The DB actor will set only if a key doesn't exist") {
      Given("DB actor does not have a specific value")
      akkaDb.cache.remove(Key)
      akkaDb.cache.get(Key) should equal (None)
      When("a value is stored")
      actorRef ! SetIfNotExists(Key, Value)
      expectMsg(SuccessfulOperation(Key))
      Then("the DB actor should have the value")
      akkaDb.cache.get(Key) should equal (Some(Value))
    }

    scenario("The DB actor will not set a key that exists") {
      Given("DB actor does not have a specific value")
      akkaDb.cache.remove(Key)
      akkaDb.cache.get(Key) should equal (None)
      When("a value is stored")
      actorRef ! SetIfNotExists(Key, Value)
      expectMsg(SuccessfulOperation(Key))
      Then("the DB actor should overwrite that value")
      actorRef ! SetIfNotExists(Key, OldValue)
      expectMsg(FailedOperation(Key))
      akkaDb.cache.get(Key) should equal (Some(Value))
    }

    scenario("The DB actor will delete a key/value pair") {
      Given("DB actor has a specific key/value pair")
      actorRef ! StoreObject(Key, Value)
      expectMsg(SuccessfulOperation(Key))
      akkaDb.cache.get(Key) should equal (Some(Value))
      When("the key is deleted")
      actorRef ! Delete(Key)
      Then("the DB actor should not have the key/value any more")
      expectMsg(SuccessfulOperation(Key))
      akkaDb.cache.get(Key) should equal (None)
    }

    scenario("The DB actor will not delete a key/value pair for a key it doesn't store") {
      Given("DB actor does not have a specific key/value pair")
      akkaDb.cache.remove(Key)
      akkaDb.cache.get(Key) should equal (None)
      When("the key is deleted")
      actorRef ! Delete(Key)
      Then("the DB actor should send a Failed operation message back")
      expectMsg(FailedOperation(Key))
    }

  }

}

