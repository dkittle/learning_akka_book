package chapter2

import org.scalatest._
import services.StringReversingService

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class StringReversingServiceSpec extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val Timeout = 2 seconds

  feature("Test the string reversing service") {

    scenario("Get a reversed string back from the string reversing service") {
      Given("a string reversing service")
      val stringReverser = new StringReversingService()
      When("a string message type is sent")
      val r = stringReverser.reverse("foo")
      Then("the service should return the reversed string")
      Await.result(r, Timeout) should be ("oof")
    }

    scenario("Get an empty string back from the string reversing service") {
      Given("a string reversing service")
      val stringReverser = new StringReversingService()
      When("an empty string is sent")
      val r = stringReverser.reverse("")
      Then("the service should return an empty string")
      Await.result(r, Timeout) should be ("")
    }

    scenario("Get a sequence of empty strings back from the string reversing service") {
      Given("a string reversing service")
      val stringReverser = new StringReversingService()
      When("a sequence of empty strings are sent")
      val r = stringReverser.reverseAll(Seq("", ""))
      Then("the service should return a sequence of empty strings")
      Await.result(r, Timeout) should be (Seq("", ""))
    }

    scenario("Get a sequence of strings back from the string reversing service") {
      Given("a string reversing service")
      val stringReverser = new StringReversingService()
      When("a sequence of strings are sent")
      val r = stringReverser.reverseAll(Seq("Ping", "Pong"))
      Then("the service should return a sequence of reversed strings")
      Await.result(r, Timeout) should be (Seq("gniP", "gnoP"))
    }

  }

}
