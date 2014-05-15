package pl.indykiewicz.devlinks

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest

class DevlinksSpec extends Specification with Specs2RouteTest with DevlinksService {
  def actorRefFactory = system
  
  "DevlinksService" should {

    "return some message for GET requests to the root path" in {
      Get() ~> rootRoute ~> check {
        responseAs[String] must contain("some")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/other") ~> rootRoute ~> check {
        handled must beFalse
      }
    }

    /*"return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(rootRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }*/

  }
}
