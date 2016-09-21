package chapter2

import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorSystem}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.concurrent.{Await, Future}

class PongActorSpec  extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FeatureSpecLike with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val actorRef = TestActorRef(PongActor.props("Pong"))
  val Tout = 2 seconds
  implicit val timeout = Timeout(5 seconds)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  feature("Test the pong actor") {
    scenario("Expect a response of Pong from a Ping") {
      Given("a pong actor")
      When("a message is sent")
      actorRef ! "Ping"
      Then("the actor should send Pong")
      expectMsg(Tout, "Pong")
    }

    def askPong(message: String): Future[String] = (actorRef ? message).mapTo[String]

    scenario("Expect a response of Pong from an ask of Ping") {
      Given("a pong actor")
      When("a message is sent using ask")
      val future = actorRef ? "Ping"
      val result = Await.result(future.mapTo[String], Tout)
      Then("the actor should send Pong")
      assert(result == "Pong")
    }

    scenario("Expect an exception with an unknown message using ask") {
      Given("a pong actor")
      When("an invalid message is sent using ask")
      val future = actorRef ? "unknown"
      Then("the actor should send an exception back")
      intercept[Exception]{
        Await.result(future.mapTo[String], 1 second)
      }
    }

    scenario("Expect a response of Pong from an ask of Ping and react using onSuccess") {
      Given("a pong actor")
      When("a message is sent using ask")
      val future = askPong("Ping")
      Then("the actor should send Pong")
      future.onSuccess(
        {
          case s: String => println(s"--Received $s back")
        }
      )
    }

    scenario("Expect a response of Pong from an ask of Ping and react using map") {
      Given("a pong actor")
      When("a message is sent using ask")
      val future = askPong("Ping")
      Then("the actor should send Pong")
      future.map(
        {
          case s: String => println(s"--Received $s back using map")
        }
      )
    }

    scenario("Chain a response of two dependent calls to pong using flatMap") {
      Given("a pong actor")
      When("two dependent messages sent using ask")
      val future = askPong("Ping").flatMap(x => askPong(x))
      Then("the actor should send Deja Vu")
      future.map(
        {
          case s: String => println(s"=--Received $s back using flatmap")
        }
      )
    }

    scenario("Handle an exception with onFailure") {
      Given("a pong actor")
      When("an invalid message is sent using ask")
      val future = askPong("unknown")
      Then("the actor should send an exception back")
      future.onFailure({
        case e: Exception => println(s"===--- Got ${e.getMessage}")
      })
    }

    scenario("Test our function to resolve two dependent pong calls") {
      Given("a pong actor")
      When("a valid message is sent using the function")
      val f = askPong("Ping").
        flatMap(x => askPong(x)).
        recover({ case e: Exception => "There was an error" })
      Then("the actor should send a valid response back")
      assert( Await.result(f.mapTo[String], Tout) == "Deja Vu")
    }

    scenario("Test our function to resolve two dependent pong calls with an error") {
      Given("a pong actor")
      When("an invalid message is sent using the function")
      val f = askPong("Foo").
        flatMap(x => askPong(x)).
        recover({ case e: Exception => "There was an error" })
      Then("the actor should send an error response back")
      assert( Await.result(f.mapTo[String], Tout) == "There was an error")
    }

    scenario("Test our function to resolve two dependent pong calls using a for comprehension") {
      Given("a pong actor")
      When("an valid message is sent using the function")
      val f = (for {
        x <- askPong("Ping")
        y <- askPong(x)
      } yield y).
        recover({ case e: Exception => "There was an error" })
      Then("the actor should send a valid response back")
      assert( Await.result(f.mapTo[String], Tout) == "Deja Vu")
    }

  }

}
