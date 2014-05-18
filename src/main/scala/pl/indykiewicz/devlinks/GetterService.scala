package pl.indykiewicz.devlinks

import akka.actor.{PoisonPill, ActorLogging, Actor}
import akka.event.LoggingReceive
import spray.routing.RequestContext

object GetterService {
  sealed class GetterServiceMessage
  case class GetLinks(ctx: RequestContext) extends GetterServiceMessage
  case class Done(links: String) extends GetterServiceMessage
}

class GetterService(requestContext: RequestContext) extends Actor with ActorLogging {
  import GetterService._
  import Boot.dzoneService

  override def receive = LoggingReceive {
    case GetLinks => {
      dzoneService ! DzoneService.GetNewDzoneNews
    }
    case Done(links) => {
      requestContext.complete(links)
      self ! PoisonPill
    }
  }
}