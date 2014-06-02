package pl.indykiewicz.devlinks

import akka.actor.{ActorRef, Props, Actor}
import spray.routing._
import spray.http.MediaTypes

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class DevlinksServiceActor extends Actor with DevlinksService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  override def receive = runRoute(rootRoute)
}

trait DevlinksService extends HttpService {

  lazy val rootRoute = {
    get {
      respondWithMediaType(MediaTypes.`application/json`) {

        def prepareGetterServiceActor(ctx: RequestContext): ActorRef = {
          actorRefFactory.actorOf(Props(creator = { () => new GetterService(ctx) }))
        }

        path("all") {
          ctx: RequestContext =>
            prepareGetterServiceActor(ctx) ! GetterService.GetLinks
        } ~
        path("dzone") {
          ctx: RequestContext =>
            prepareGetterServiceActor(ctx) ! GetterService.GetDzoneLinks
        } ~
        path("infoq") {
          ctx: RequestContext =>
            prepareGetterServiceActor(ctx) ! GetterService.GetInfoQLinks
        } ~
        path("hn") {
          ctx: RequestContext =>
            prepareGetterServiceActor(ctx) ! GetterService.GetHackerNewsLinks
        } ~
        path("reddit") {
          ctx: RequestContext =>
            prepareGetterServiceActor(ctx) ! GetterService.GetRedditLinks
        }

      }
    }

  }
}