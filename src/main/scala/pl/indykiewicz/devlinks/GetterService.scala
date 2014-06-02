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
  case class GetInfoQLinks(ctx: RequestContext) extends GetterServiceMessage
  case class GetHackerNewsLinks(ctx: RequestContext) extends GetterServiceMessage
  case class GetRedditLinks(ctx: RequestContext) extends GetterServiceMessage
  case class Done(links: List[Devlink]) extends GetterServiceMessage
}

class GetterService(requestContext: RequestContext) extends Actor with ActorLogging {
  import GetterService._
  import Boot.system

  var workers = new HashSet[ActorRef]
  var allLinks : List[Devlink] = List.empty

  private def getNews(props: Props) {
    val service = system.actorOf(props)
    workers += service
    service ! NewsService.GetNews
  }

  override def receive = LoggingReceive {
    case GetLinks => {
      val sources = List(Props[DzoneService], Props[HackerNewsService], Props[InfoQService], Props[JavaRedditService], Props[ScalaRedditService], Props[ProgrammingRedditService])
      sources.foreach(getNews)
    }
    case GetDzoneLinks => {
      val sources = List(Props[DzoneService])
      sources.foreach(getNews)
    }
    case GetInfoQLinks => {
      val sources = List(Props[InfoQService])
      sources.foreach(getNews)
    }
    case GetHackerNewsLinks => {
      val sources = List(Props[HackerNewsService])
      sources.foreach(getNews)
    }
    case GetRedditLinks => {
      val sources = List(Props[JavaRedditService], Props[ScalaRedditService], Props[ProgrammingRedditService])
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
