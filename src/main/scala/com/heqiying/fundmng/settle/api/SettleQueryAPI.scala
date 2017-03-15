package com.heqiying.fundmng.settle.api

import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.heqiying.fundmng.settle.common.LazyLogging
import com.heqiying.fundmng.settle.service.SettleApp
import com.heqiying.fundmng.settle.utils.AppError
import io.swagger.annotations.{ Api, ApiImplicitParam, ApiImplicitParams, ApiOperation }

import scala.util.{ Failure, Success }
import spray.json._

@Api(value = "Settle Query API", consumes = "application/json", produces = "application/json")
@Path("/api/v1/settles")
class SettleQueryAPI(implicit val app: SettleApp, val system: ActorSystem, val mat: ActorMaterializer) extends LazyLogging {

  def routes = getSettlesRoute ~ getSettleRoute ~ getSharesRoute ~ getTradesRoute ~
    getTradeSummariesRoute ~ getStatementsRoute ~ getRemitsRoute

  @ApiOperation(value = "get settle history for fund", nickname = "get settle history on fundUuid", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "fundUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{fundUuid}/settles")
  def getSettlesRoute = path("api" / "v1" / "settles" / Segment / "settles") { fundUuid =>
    import com.heqiying.fundmng.settle.model.SettleJsonSupport._
    get {
      onComplete(app.getSettles(fundUuid)) {
        case Success(settles) => complete(settles)
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Get settles failed! fundUuid: $fundUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Get settles failed! fundUuid: $fundUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

  @ApiOperation(value = "get settle for fund&open date", nickname = "get settle on fundUuid+opendate", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "fundUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = ""),
    new ApiImplicitParam(name = "openDate", value = """2017-03-31""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{fundUuid}/{openDate}/settle")
  def getSettleRoute = path("api" / "v1" / "settles" / Segment / Segment / "settle") { (fundUuid, openDate) =>
    import com.heqiying.fundmng.settle.model.SettleJsonSupport._
    get {
      onComplete(app.getSettle(fundUuid, openDate)) {
        case Success(settle) => complete(settle)
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Get settle failed! fundUuid: $fundUuid, openDate: $openDate, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Get settle failed! fundUuid: $fundUuid, openDate: $openDate, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

  @ApiOperation(value = "get shares", nickname = "get shares on settleUuid", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "settleUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{settleUuid}/shares")
  def getSharesRoute = path("api" / "v1" / "settles" / Segment / "shares") { settleUuid =>
    import com.heqiying.fundmng.settle.model.SettleJsonSupport._
    get {
      onComplete(app.getShares(settleUuid)) {
        case Success(shares) => complete(shares)
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Get shares failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Get shares failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

  @ApiOperation(value = "get trades", nickname = "get trades on settleUuid", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "settleUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{settleUuid}/trades")
  def getTradesRoute = path("api" / "v1" / "settles" / Segment / "trades") { settleUuid =>
    import com.heqiying.fundmng.settle.model.SettleJsonSupport._
    get {
      onComplete(app.getTrades(settleUuid)) {
        case Success(trades) => complete(trades)
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Get trades failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Get trades failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

  @ApiOperation(value = "get trade summaries", nickname = "get trade summaries on settleUuid", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "settleUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{settleUuid}/tradeSummaries")
  def getTradeSummariesRoute = path("api" / "v1" / "settles" / Segment / "tradeSummaries") { settleUuid =>
    import com.heqiying.fundmng.settle.model.SettleJsonSupport._
    get {
      onComplete(app.getTradeSummaries(settleUuid)) {
        case Success(tradeSummaries) => complete(tradeSummaries)
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Get tradeSummaries failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Get tradeSummaries failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

  @ApiOperation(value = "get invest statements", nickname = "get invest statements on settleUuid", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "settleUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{settleUuid}/statements")
  def getStatementsRoute = path("api" / "v1" / "settles" / Segment / "statements") { settleUuid =>
    import com.heqiying.fundmng.settle.model.SettleJsonSupport._
    get {
      onComplete(app.getInvestStatements(settleUuid)) {
        case Success(statements) => complete(statements)
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Get statements failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Get statements failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }

  @ApiOperation(value = "get remits", nickname = "get remits on settleUuid", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "settleUuid", value = """...""", required = true, dataType = "string", paramType = "path", defaultValue = "")
  ))
  @Path("/{settleUuid}/remits")
  def getRemitsRoute = path("api" / "v1" / "settles" / Segment / "remits") { settleUuid =>
    import com.heqiying.fundmng.settle.model.SettleJsonSupport._
    get {
      onComplete(app.getRemits(settleUuid)) {
        case Success(remits) => complete(remits)
        case Failure(e: AppError) =>
          import com.heqiying.fundmng.settle.utils.AppErrorJsonProtocol._
          logger.error(s"Get remits failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.BadRequest, entity = e.toJson.compactPrint))
        case Failure(e) =>
          logger.error(s"Get remits failed! settleUuid: $settleUuid, Reason: $e")
          complete(HttpResponse(StatusCodes.InternalServerError))
      }
    }
  }
}
