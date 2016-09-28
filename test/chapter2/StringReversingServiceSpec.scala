package chapter2

import org.scalatest._
import services.StringReversingService

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class StringReversingServiceSpec extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val Timeout = 2 seconds

  feature("Test the string reversing service") {

    scenario("Get a reversed string back from the string reversing actor") {
      Given("a string reversing actor actor")
      val stringReverser = new StringReversingService()
      When("a string message type is sent")
      val r = stringReverser.reverse("foo")
      Then("the actor should send the reversed string message back")
      Await.result(r, Timeout) should be ("oof")
    }

    scenario("Get an empty string back from the string reversing actor") {
      Given("a string reversing actor actor")
      val stringReverser = new StringReversingService()
      When("an empty string is sent")
      val r = stringReverser.reverse("")
      Then("the actor should send an empty string message back")
      Await.result(r, Timeout) should be ("")
    }

  }

}
