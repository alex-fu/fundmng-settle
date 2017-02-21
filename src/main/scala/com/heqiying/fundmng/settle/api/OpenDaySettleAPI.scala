package com.heqiying.fundmng.settle.api

import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import com.heqiying.fundmng.settle.common.LazyLogging
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.heqiying.fundmng.settle.service.SettleApp
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._

import scala.util.{Failure, Success}

@Api(value = "OpenDay Settle API", consumes = "application/json", produces = "application/json")
@Path("/api/v1/settles")
class OpenDaySettleAPI(implicit val app: SettleApp, val system: ActorSystem, val mat: ActorMaterializer) extends LazyLogging {
  val routes = settleRoute

  case class OpenDaySettleRequest(fundUuid: String, openDate: String)
  object OpenDaySettleRequestJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val reqJsonFormat = jsonFormat2(OpenDaySettleRequest.apply)
  }

  @ApiOperation(value = "settle", nickname = "openday settle", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "request", value = """ {"fundUuid": "...", "openDate": "20170331"}""", required = true, dataType = "string", paramType = "body", defaultValue = "")
  ))
  def settleRoute = path("api" / "v1" / "settles") {
    post {
      import OpenDaySettleRequestJsonSupport._
      entity(as[OpenDaySettleRequest]) { req =>
        onComplete(app.opendaySettle(req.fundUuid, req.openDate)) {
          case Success(Right(settleUuid)) => complete(HttpResponse(StatusCodes.OK))
          case Success(Left(e)) =>
            logger.error(s"Openday settle failed! fundUuid: ${req.fundUuid}, openDate: ${req.openDate}, Reason: $e")
            complete(HttpResponse(StatusCodes.BadRequest, entity = e))
          case Failure(e) =>
            logger.error(s"Openday settle failed! fundUuid: ${req.fundUuid}, openDate: ${req.openDate}, Reason: $e")
            complete(HttpResponse(StatusCodes.InternalServerError))
        }
      }
    }
  }
}
