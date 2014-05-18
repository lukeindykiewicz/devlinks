package pl.indykiewicz.devlinks

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive

object DzoneService {
  sealed class DzoneServiceMessage
  case class GetNewDzoneNews() extends DzoneServiceMessage
}

class DzoneService extends Actor with ActorLogging {

  import DzoneService._

  override def receive = LoggingReceive {
    case GetNewDzoneNews => {
      sender ! GetterService.Done("some new message ")
    }
  }

}