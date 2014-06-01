package pl.indykiewicz.devlinks

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive
import scala.concurrent.{Await, Future}
import spray.http._
import spray.client.pipelining._
import scala.concurrent.duration._

object NewsService {
  sealed class NewsServiceMessage
  case class GetNews() extends NewsServiceMessage
}

trait NewsService extends Actor with ActorLogging {

  import NewsService._

  implicit val system = Boot.system
  import system.dispatcher

  def url : String
  def extractNews : HttpResponse => Seq[Devlink]

  override def receive = LoggingReceive {
    case GetNews => {

      val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
      val responseFuture: Future[HttpResponse] = pipeline(Get(url))
      val response = Await.result(responseFuture, 20 seconds)

      sender ! GetterService.Done(extractNews(response))
    }
  }

}

class DzoneService extends NewsService {
  override def url = "http://feeds.dzone.com/dzone/frontpage"
  override def extractNews : HttpResponse => Seq[Devlink] = {
    response =>
      val dzoneXml = scala.xml.XML.loadString(response.entity.data.asString)
      (dzoneXml \ "channel" \ "item" ) map { x => (x \ "title", x \ "description", x \ "link")  } map {case (t,d,l) => Devlink(t.text, l.text, d.text)}
  }
}
