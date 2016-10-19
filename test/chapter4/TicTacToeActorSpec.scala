package chapter4

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import chapter4.TicTacToeActor._
import org.scalatest.{BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers}

import scala.concurrent.duration._

class TicTacToeActorSpec extends TestKit(ActorSystem("test-system")) with ImplicitSender
  with FeatureSpecLike with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val timeout = 2 seconds

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  feature("Test the tic tac toe actor") {

    scenario("Expect a the first play to be X") {
      Given("a tictactoe actor")
      val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
      When("a WhoPlays? message is sent")
      actorRef ! WhoPlays
      Then("the actor should send XPlays")
      expectMsg(timeout, XPlays)
      actorRef.stop()
    }

    scenario("Playing on position -1 should be illegal") {
      Given("a tictactoe actor")
      val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
      When("a play to -1 is sent")
      actorRef ! Play(-1)
      Then("an illegal move should be returned")
      expectMsg(timeout, IllegalMove)
      actorRef.stop()
    }

    scenario("Playing position 9 should be illegal") {
      Given("a tictactoe actor")
      val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
      When("a play to 9 is sent")
      actorRef ! Play(9)
      Then("an illegal move should be returned")
      expectMsg(timeout, IllegalMove)
      actorRef.stop()
    }

    scenario("Playing on position 1 twice should not be allowed") {
      Given("a play to position 1")
      val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
      actorRef ! Play(1)
      expectMsg(timeout, OPlays)
      When("a play to 1 is sent")
      actorRef ! Play(1)
      Then("an illegal move should be returned")
      expectMsg(timeout, PositionOccupied)
      actorRef.stop()
    }

    scenario("Playing on position 1 and then 2 should be allowed") {
      Given("a play to position 1")
      val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
      actorRef ! Play(1)
      expectMsg(timeout, OPlays)
      When("a play to 2 is sent")
      actorRef ! Play(2)
      Then("an XPlays message should be returned")
      expectMsg(timeout, XPlays)
      actorRef.stop()
    }

    scenario("X should be able to win a game on the top row") {
      Given("a tictactoe actor")
      val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
      When("X plays the top row")
      playWinningGame(actorRef)
      Then("an XWon message should be returned")
      expectMsg(timeout, XWon)
      actorRef.stop()
    }

    scenario("You cannot continue playing a game that is over") {
      Given("a tictactoe actor")
      val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
      When("X wins the game")
      playWinningGame(actorRef)
      expectMsg(timeout, XWon)
      Then("Any message sent to the actor should return a GameOver message")
      actorRef ! Play(6)
      expectMsg(timeout, GameOver)
      actorRef.stop()
    }

    scenario("If noone wins, the game will still end") {
      Given("a tictactoe actor")
      val actorRef = TestActorRef(Props(classOf[TicTacToeActor]))
      When("Noone wins the game")
      actorRef ! Play(0)
      expectMsg(timeout, OPlays)
      actorRef ! Play(1)
      expectMsg(timeout, XPlays)
      actorRef ! Play(2)
      expectMsg(timeout, OPlays)

      actorRef ! Play(3)
      expectMsg(timeout, XPlays)
      actorRef ! Play(5)
      expectMsg(timeout, OPlays)
      actorRef ! Play(4)
      expectMsg(timeout, XPlays)

      actorRef ! Play(7)
      expectMsg(timeout, OPlays)
      actorRef ! Play(8)
      expectMsg(timeout, XPlays)
      actorRef ! Play(6)

      Then("a NoWinner message should be returned")
      expectMsg(timeout, NoWinner)
      actorRef ! Play(1)
      expectMsg(timeout, GameOver)
      actorRef.stop()
    }

  }

  private def playWinningGame(actorRef: ActorRef): Unit = {
    actorRef ! Play(0)
    expectMsg(timeout, OPlays)
    actorRef ! Play(3)
    expectMsg(timeout, XPlays)
    actorRef ! Play(1)
    expectMsg(timeout, OPlays)
    actorRef ! Play(4)
    expectMsg(timeout, XPlays)
    actorRef ! Play(2)
  }

}
