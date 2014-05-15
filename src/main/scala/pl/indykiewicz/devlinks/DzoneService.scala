package pl.indykiewicz.devlinks

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive

object DzoneServiceActor {
  sealed class DzoneMessages
  case class GetNewDzoneNews()
}

class DzoneServiceActor extends Actor with ActorLogging {

  import DzoneServiceActor._

  var counter = 0

  override def receive = LoggingReceive {
    case GetNewDzoneNews => {
      counter += 1
      sender ! GetterActor.Done("some new message " + counter)
    }
  }

}