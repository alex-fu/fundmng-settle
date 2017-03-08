package com.heqiying.fundmng.settle.service

import java.sql.Date

import akka.actor.{ Actor, ActorRef }
import com.heqiying.fundmng.settle.common.LazyLogging
import com.heqiying.fundmng.settle.domain._
import com.heqiying.fundmng.settle.model.Settles
import com.heqiying.fundmng.settle.utils.{ AppError, AppErrors }

import scala.concurrent.{ Await, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import scala.concurrent.duration._

object SettleProtocols {
  sealed abstract class AbstractSettle
  case class OpenDaySettle(fundUuid: String, openDate: Date) extends AbstractSettle
  case class DropSettle(fundUuid: String, settleUuid: String) extends AbstractSettle
  case class ConfirmSettle(fundUuid: String, settleUuid: String) extends AbstractSettle
  case object ChangeShareSettle extends AbstractSettle

  case object Initialize
}

class SettleActor extends Actor with LazyLogging {
  import SettleProtocols._

  private[this] val opendaySettleService =
    new SettleDomainService with OpenDayParamChecker with OpenDayTradeAggregator with TradeCheckerImpl with SettleCalculatorImpl with SettleRepositoryImpl
  private[this] val timeout = 600.seconds

  override def preStart() = {
    self ! Initialize
    super.preStart()
  }

  override def receive = {
    case Initialize =>
      logger.info("SettleActor started!")

    case OpenDaySettle(fundUuid, openDate) =>
      logger.info(s"Received OpenDaySettle($fundUuid, $openDate)")
      val from = sender()
      val f = opendaySettleService.getLatestSettle(fundUuid).map {
        case Some(x) if x.state == Settles.SettledState =>
          from ! AppErrors.AlreadyInSettling(s"""{"fundUuid": "$fundUuid","openDate": "$openDate"""")
        case _ =>
          val f0 = opendaySettleService.settle(OpenDaySettleParam(fundUuid, openDate))
          f0.onComplete {
            case Success(settleUuid) => from ! settleUuid
            case Failure(e: AppError) => from ! e
            case Failure(e) => from ! AppErrors.UnknownSettleError(s"$e")
          }
          Await.ready(f0, timeout)
      }
      Await.ready(f, timeout)

    case DropSettle(fundUuid, settleUuid) =>
      logger.info(s"Received DropSettle($fundUuid, $settleUuid)")
      val from = sender()
      val f = opendaySettleService.getLatestSettle(fundUuid).map {
        case Some(x) if x.uuid == settleUuid && x.state == Settles.SettledState =>
          val f0 = opendaySettleService.dropSettle(x)
          f0.onComplete {
            case Success(settleUuid_) => from ! settleUuid_
            case Failure(e: AppError) => from ! e
            case Failure(e) => from ! AppErrors.UnknownSettleError(s"$e")
          }
          Await.ready(f0, timeout)
        case _ => from ! AppErrors.CannotDropSettleError(s"""{"fundUuid": "$fundUuid","settleUuid": "$settleUuid"""")
      }
      Await.ready(f, timeout)

    case ConfirmSettle(fundUuid, settleUuid) =>
      logger.info(s"Received ConfirmSettle($fundUuid, $settleUuid)")
      val from = sender()
      val f = opendaySettleService.getLatestSettle(fundUuid).map {
        case Some(x) if x.uuid == settleUuid && x.state == Settles.SettledState =>
          val f0 = opendaySettleService.confirmSettle(x)
          f0.onComplete {
            case Success(settleUuid_) => from ! settleUuid_
            case Failure(e: AppError) => from ! e
            case Failure(e) => from ! AppErrors.UnknownSettleError(s"$e")
          }
          Await.ready(f0, timeout)
        case _ => from ! AppErrors.CannotConfirmSettleError(s"""{"fundUuid": "$fundUuid","settleUuid": "$settleUuid"""")
      }
      Await.ready(f, timeout)
  }

}
