package pl.indykiewicz.devlinks

import akka.actor._
import akka.event.LoggingReceive
import org.json4s.native.Serialization
import scala.collection.immutable.HashSet
import spray.routing.RequestContext

object GetterService {
  sealed class GetterServiceMessage
  case class GetLinks(ctx: RequestContext) extends GetterServiceMessage
  case class GetDzoneLinks(ctx: RequestContext) extends GetterServiceMessage
  case class Done(links: List[Devlink]) extends GetterServiceMessage
}

class GetterService(requestContext: RequestContext) extends Actor with ActorLogging {
  import GetterService._
  import Boot.system

  var workers = new HashSet[ActorRef]
  var allLinks : List[Devlink] = List.empty

  override def receive = LoggingReceive {
    case GetLinks => {

      def getNews(props: Props) {
        val service = system.actorOf(props)
        workers += service
        service ! NewsService.GetNews
      }

      val sources = List(Props[DzoneService], Props[HackerNewsService], Props[InfoQService], Props[JavaRedditService], Props[JavaRedditService], Props[JavaRedditService])
      sources.foreach(getNews)
    }
    case GetDzoneLinks => {

      def getNews(props: Props) {
        val service = system.actorOf(props)
        workers += service
        service ! NewsService.GetNews
      }

      val sources = List(Props[DzoneService])
      sources.foreach(getNews)
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
