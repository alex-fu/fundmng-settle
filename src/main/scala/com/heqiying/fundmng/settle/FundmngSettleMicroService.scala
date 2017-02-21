package com.heqiying.fundmng.settle

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.heqiying.fundmng.settle.interface.{ ApiHttpInterface, HttpLoggerInterface, SwaggerDocService }
import com.heqiying.fundmng.settle.common.{ AppConfig, LazyLogging }
import com.heqiying.fundmng.settle.service.SettleApp

import scala.concurrent.Await
import scala.concurrent.duration._

object FundmngSettleMicroService extends App with LazyLogging {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher
  val timeout = 10.seconds

  implicit val settleApp = new SettleApp
  val swaggerDocService = new SwaggerDocService(system, mat)
  val apiInterface = new ApiHttpInterface()

  val routes = HttpLoggerInterface.logRoute(swaggerDocService.docsRoute ~ apiInterface.route)
  val httpBindingFuture = Http().bindAndHandle(routes, "0.0.0.0", AppConfig.app.admin.port)
  logger.info(s"""Server online at http://0.0.0.0:${AppConfig.app.admin.port}/ ...""")

  sys addShutdownHook {
    logger.info(s"""Server will shutdown ...""")
    Await.ready(httpBindingFuture.map(_.unbind()), timeout)
  }

  try {
    val stream = getClass.getResourceAsStream("/issue.txt")
    val text = scala.io.Source.fromInputStream(stream).mkString
    stream.close()
    println(text)
  } finally {}
}
