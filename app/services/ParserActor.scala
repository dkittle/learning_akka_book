package services

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef}
import chapter1.AkkaDb.StoreObject
import de.l3s.boilerpipe.extractors.ArticleExtractor

object ParserActor {

}

class ParserActor @Inject()(@Named("akkaDb") akkaDb: ActorRef) extends Actor with ActorLogging {

  val extractor = ArticleExtractor.INSTANCE

  def receive = {
    case x: String => {
      val text = extractor.getText(new java.net.URL(x))
      akkaDb ! StoreObject(x, text)
    }
  }

}
