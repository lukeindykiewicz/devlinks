package pl.indykiewicz.devlinks

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App with DevlinksServicesModule with ServerStartup

trait DevlinksServicesModule {
  implicit val system = ActorSystem("devlinks-actors")
  val devlinksService = system.actorOf(Props[DevlinksServiceActor], "devlinks-actor")
  val dzoneService = system.actorOf(Props[DzoneService], "dzone-actor")
}

trait ServerStartup {
  devlinksServicesModule: DevlinksServicesModule =>

  IO(Http) ! Http.Bind(devlinksService, interface = "localhost", port = 8080)
}
