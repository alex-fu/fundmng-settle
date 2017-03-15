package com.heqiying.fundmng.settle.interface

import scala.reflect.runtime.universe._
import akka.actor._
import akka.stream.ActorMaterializer
import com.github.swagger.akka.{ HasActorSystem, SwaggerHttpService }
import com.github.swagger.akka.model.Info
import com.heqiying.fundmng.settle.api._
import com.heqiying.fundmng.settle.common.LazyLogging

class SwaggerDocService(system: ActorSystem, mat: ActorMaterializer) extends SwaggerHttpService with HasActorSystem with LazyLogging {
  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = mat
  override val apiDocsPath = "api" //where you want the swagger-json endpoint exposed
  override val info = Info() //provides license and other description details

  override val apiTypes = Seq(
    typeOf[OpenDaySettleAPI],
    typeOf[SettleQueryAPI]
  )

  val docsRoute = get {
    path("") {
      pathEndOrSingleSlash {
        logger.info("retrieve root swagger docs")
        getFromResource("swagger-ui/index.html")
      }
    } ~ getFromResourceDirectory("swagger-ui")
  } ~ routes

}
