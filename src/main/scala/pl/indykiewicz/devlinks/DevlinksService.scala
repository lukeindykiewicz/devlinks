package pl.indykiewicz.devlinks

import akka.actor.{Props, Actor}
import spray.routing._

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
      path("") {
        ctx: RequestContext =>
          val getterActor = actorRefFactory.actorOf(Props(creator = { () => new GetterActor(ctx) }))
          getterActor ! GetterActor.GetLinks
      }
    }
  }

}