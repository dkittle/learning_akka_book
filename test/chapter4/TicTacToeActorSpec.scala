package chapter4

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import chapter4.TicTacToeActor._
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.concurrent.duration._

class TicTacToeActorSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FeatureSpecLike with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
  val timeout = 2 seconds

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  feature("Test the tic tac toe actor") {

    scenario("Expect a the first play to be X") {
      Given("a tictactoe actor")
      When("a WhoPlays? message is sent")
      actorRef ! WhoPlays
      Then("the actor should send XPlays")
      expectMsg(timeout, XPlays)
    }

    scenario("Playing on position -1 should be illegal") {
      Given("a tictactoe actor")
      When("a play to -1 is sent")
      actorRef ! Play(-1)
      Then("an illegal move should be returned")
      expectMsg(timeout, IllegalMove)
    }

    scenario("Playing position 9 should be illegal") {
      Given("a tictactoe actor")
      When("a play to 9 is sent")
      actorRef ! Play(9)
      Then("an illegal move should be returned")
      expectMsg(timeout, IllegalMove)
    }

    scenario("Playing on position 1 twice should not be allowed") {
      Given("a play to position 1")
      actorRef ! Play(1)
      expectMsg(timeout, OPlays)
      When("a play to 1 is sent")
      actorRef ! Play(1)
      Then("an illegal move should be returned")
      expectMsg(timeout, PositionOccupied)
    }

  }

}
