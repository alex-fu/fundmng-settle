package com.heqiying.fundmng.settle.api

import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.heqiying.fundmng.settle.common.LazyLogging
import com.heqiying.fundmng.settle.service.SettleApp
import com.heqiying.fundmng.settle.utils.AppError
import io.swagger.annotations.{ Api, ApiImplicitParam, ApiImplicitParams, ApiOperation }
import spray.json.{ DefaultJsonProtocol, _ }

import scala.util.{ Failure, Success }

@Api(value = "OpenDay Settle API", consumes = "application/json", produces = "application/json")
@Path("/api/v1/settles")
class OpenDaySettleAPI(implicit val app: SettleApp, val system: ActorSystem, val mat: ActorMaterializer) extends LazyLogging {
  val routes = settleRoute ~ dropSettledRoute ~ confirmSettledRoute

  case class OpenDaySettleRequest(fundUuid: String, openDate: String)

  object OpenDaySettleRequestJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val reqJsonFormat = jsonFormat2(OpenDaySettleRequest.apply)
  }

  @ApiOperation(value = "settle", nickname = "openday settle", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "fundUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = ""),
    new ApiImplicitParam(name = "openDate", value = """2017-03-31""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{fundUuid}/{openDate}/settle")
  def settleRoute = path("api" / "v1" / "settles" / Segment / Segment / "settle") { (fundUuid, openDate) =>
    import DefaultJsonProtocol._
    import SprayJsonSupport._
    post {
      onComplete(app.opendaySettle(fundUuid, openDate)) {
        case Success(settleUuid) => complete(Map("settleUuid" -> settleUuid))
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Openday settle failed! fundUuid: $fundUuid, openDate: $openDate, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Openday settle failed! fundUuid: $fundUuid, openDate: $openDate, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

  @ApiOperation(value = "dropSettle", nickname = "drop settle", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "fundUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = ""),
    new ApiImplicitParam(name = "settleUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{fundUuid}/{settleUuid}/dropSettled")
  def dropSettledRoute = path("api" / "v1" / "settles" / Segment / Segment / "dropSettled") { (fundUuid, settleUuid) =>
    post {
      onComplete(app.dropSettle(fundUuid, settleUuid)) {
        case Success(_) => complete(HttpResponse(StatusCodes.OK))
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Drop settle failed! fundUuid: $fundUuid, settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Drop settle failed! fundUuid: $fundUuid, settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

  @ApiOperation(value = "confirmSettle", nickname = "confirm settle", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "fundUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = ""),
    new ApiImplicitParam(name = "settleUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{fundUuid}/{settleUuid}/confirmation")
  def confirmSettledRoute = path("api" / "v1" / "settles" / Segment / Segment / "confirmation") { (fundUuid, settleUuid) =>
    post {
      onComplete(app.confirmSettle(fundUuid, settleUuid)) {
        case Success(_) => complete(HttpResponse(StatusCodes.OK))
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Confirm settle failed! fundUuid: $fundUuid, settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Confirm settle failed! fundUuid: $fundUuid, settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

}
