package pl.indykiewicz.devlinks

import akka.actor.{Props, PoisonPill, ActorLogging, Actor}
import akka.event.LoggingReceive
import spray.routing.RequestContext

object GetterActor {
  sealed class GetterActorMessage
  case class GetLinks(ctx: RequestContext)
  case class Done(links: String)
}

class GetterActor(requestContext: RequestContext) extends Actor with ActorLogging {
  import GetterActor._
  import Boot.dzoneService

  override def receive = LoggingReceive {
    case GetLinks => {
      dzoneService ! DzoneServiceActor.GetNewDzoneNews
    }
    case Done(links) => {
      requestContext.complete(links)
      self ! PoisonPill
    }
  }
}