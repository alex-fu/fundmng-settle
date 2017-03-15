package com.heqiying.fundmng.settle.interface

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.heqiying.fundmng.settle.api._
import com.heqiying.fundmng.settle.common.{ AppConfig, LazyLogging }
import com.heqiying.fundmng.settle.directives.UsefulDirective
import com.heqiying.fundmng.settle.service.SettleApp

class ApiHttpInterface(implicit val app: SettleApp, val system: ActorSystem, val mat: ActorMaterializer) extends LazyLogging {
  private[this] val routes = Seq(
    new OpenDaySettleAPI routes,
    new SettleQueryAPI routes
  )

  val r0 = routes.reduceLeft {
    _ ~ _
  }
  val route = extractMethod { method =>
    extractUri { uri =>
      UsefulDirective.extractAccesser {
        case None if AppConfig.app.api.authorization => UsefulDirective.forbiddenRoute
        case _ => r0 ~ UsefulDirective.notFoundRoute
      }
    }
  }
}