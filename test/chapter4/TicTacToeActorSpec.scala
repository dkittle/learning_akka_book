package chapter4

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import chapter4.TicTacToeActor.{WhoPlays, XPlays}
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class TicTacToeActorSpec  extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FeatureSpecLike with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
  val Tout = 2 seconds
  implicit val timeout = Timeout(5 seconds)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  feature("Test the tic tac toe actor") {
    scenario("Expect a the first play to be X") {
      Given("a tictactoe actor")
      When("a WhoPlays? message is sent")
      actorRef ! WhoPlays
      Then("the actor should send XPlays")
      expectMsg(Tout, XPlays)
    }
  }

}
