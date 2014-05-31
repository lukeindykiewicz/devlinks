package pl.indykiewicz.devlinks

import akka.actor.{PoisonPill, ActorLogging, Actor}
import akka.event.LoggingReceive
import spray.routing.RequestContext
import org.json4s.native.Serialization

object GetterService {
  sealed class GetterServiceMessage
  case class GetLinks(ctx: RequestContext) extends GetterServiceMessage
  case class Done(links: Seq[HackUrl]) extends GetterServiceMessage
}

class GetterService(requestContext: RequestContext) extends Actor with ActorLogging {
  import GetterService._
  import Boot.dzoneService

  override def receive = LoggingReceive {
    case GetLinks => {
      dzoneService ! DzoneService.GetNewDzoneNews
    }
    case Done(links) => {

      import org.json4s._
      import org.json4s.native.Serialization._
      implicit val formats = Serialization.formats(NoTypeHints)

      requestContext.complete(writePretty(links))
      self ! PoisonPill
    }
  }
}