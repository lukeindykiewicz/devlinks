package pl.indykiewicz.devlinks

import akka.actor._
import akka.event.LoggingReceive
import org.json4s.native.Serialization
import scala.collection.immutable.HashSet
import spray.routing.RequestContext

object GetterService {
  sealed class GetterServiceMessage
  case class GetLinks(ctx: RequestContext) extends GetterServiceMessage
  case class Done(links: List[Devlink]) extends GetterServiceMessage
}

class GetterService(requestContext: RequestContext) extends Actor with ActorLogging {
  import GetterService._
  import Boot.system

  var workers = new HashSet[ActorRef]
  var allLinks : List[Devlink] = List.empty

  override def receive = LoggingReceive {
    case GetLinks => {
      val dzoneService = system.actorOf(Props[DzoneService])
      workers += dzoneService
      dzoneService ! NewsService.GetNews

      val hNService = system.actorOf(Props[HackerNewsService])
      workers += hNService
      hNService ! NewsService.GetNews

      val infoQService = system.actorOf(Props[InfoQService])
      workers += infoQService
      infoQService ! NewsService.GetNews

      val javaRedditService = system.actorOf(Props[JavaRedditService])
      workers += javaRedditService
      javaRedditService ! NewsService.GetNews

      val scalaRedditService = system.actorOf(Props[JavaRedditService])
      workers += scalaRedditService
      scalaRedditService ! NewsService.GetNews

      val programmingRedditService = system.actorOf(Props[JavaRedditService])
      workers += programmingRedditService
      programmingRedditService ! NewsService.GetNews
    }
    case Done(links) => {

      import org.json4s._
      import org.json4s.native.Serialization._
      implicit val formats = Serialization.formats(NoTypeHints)

      workers -= sender
      allLinks = allLinks ::: links

      if (workers.isEmpty) {
        requestContext.complete(writePretty(allLinks))
        self ! PoisonPill
      }
    }
  }
}
