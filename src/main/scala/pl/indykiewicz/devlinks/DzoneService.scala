package pl.indykiewicz.devlinks

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive
import scala.concurrent.{Await, Future}
import spray.http._
import spray.client.pipelining._
import scala.concurrent.duration._


object DzoneService {
  sealed class DzoneServiceMessage
  case class GetNewDzoneNews() extends DzoneServiceMessage
}

class DzoneService extends Actor with ActorLogging {

  import DzoneService._

  implicit val system = Boot.system
  import system.dispatcher

  override def receive = LoggingReceive {
    case GetNewDzoneNews => {

      val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
      val responseFuture: Future[HttpResponse] = pipeline(Get("http://feeds.dzone.com/dzone/frontpage"))

      val response = Await.result(responseFuture, 20 seconds)
      val dzoneXml = scala.xml.XML.loadString(response.entity.data.asString)
      val result =  (dzoneXml \ "channel" \ "item" ) map { x => (x \ "title", x \ "description", x \ "link")  } map {x => (x._1.text, x._3.text, "\n")}

      sender ! GetterService.Done("some messages \n " + result mkString)
    }
  }

}
