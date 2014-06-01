package pl.indykiewicz.devlinks

import akka.actor.{PoisonPill, ActorLogging, Actor}
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
  def extractNews : HttpResponse => List[Devlink] = {
    response =>
    val xml = scala.xml.XML.loadString(response.entity.data.asString)
    ((xml \ "channel" \ "item" ) map { x => (x \ "title", x \ "description", x \ "link")  } map {case (t,d,l) => Devlink(t.text, l.text, d.text)}).toList
  }

  override def receive = LoggingReceive {
    case GetNews => {

      val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
      val responseFuture: Future[HttpResponse] = pipeline(Get(url))
      val response = Await.result(responseFuture, 20 seconds)

      sender ! GetterService.Done(extractNews(response))
      self ! PoisonPill
    }
  }

}

class DzoneService extends NewsService {
  override def url = "http://feeds.dzone.com/dzone/frontpage"
}

class HackerNewsService extends NewsService {
  override def url = "https://news.ycombinator.com/rss"
}

class InfoQService extends NewsService {
  override def url = "http://www.infoq.com/feed"
}

class JavaRedditService extends NewsService {
  override def url = "http://www.reddit.com/r/java/.rss"
}

class ScalaRedditService extends NewsService {
  override def url = "http://www.reddit.com/r/scala/.rss"
}

class ProgrammingRedditService extends NewsService {
  override def url = "http://www.reddit.com/r/programming/.rss"
}
