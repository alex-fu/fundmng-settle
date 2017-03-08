package com.heqiying.fundmng.settle.service

import java.sql.Date

import akka.actor.ActorRef
import akka.pattern.{ AskTimeoutException, ask }
import akka.util.Timeout
import com.heqiying.fundmng.settle.common.LazyLogging
import com.heqiying.fundmng.settle.service.SettleProtocols.{ ConfirmSettle, DropSettle, OpenDaySettle }
import com.heqiying.fundmng.settle.utils.{ AppError, AppErrors }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success, Try }

trait SettleService extends LazyLogging {
  def settleActorRef: ActorRef

  implicit val timeout: Timeout = 6000.seconds

  def opendaySettle(fundUuid: String, openDate: String): Future[String] = {
    Try(Date.valueOf(openDate)) match {
      case Success(date) =>
        (settleActorRef ? OpenDaySettle(fundUuid, date)) flatMap {
          case settleUuid: String => Future.successful(settleUuid)
          case e: AppError => Future.failed(e)
        } recoverWith {
          case e if !e.isInstanceOf[AppError] =>
            logger.error(s"Openday settle failed! Reason: $e")
            Future.failed(AppErrors.UnknownSettleError(e.toString))
        }
      case Failure(_) =>
        logger.error(s"Transform open date($openDate) failed! should be like 'yyyy-mm-dd'")
        Future.failed(AppErrors.InvalidOpenDay())
    }
  }

  def dropSettle(fundUuid: String, settleUuid: String): Future[String] = {
    (settleActorRef ? DropSettle(fundUuid, settleUuid)) flatMap {
      case settleUuid: String => Future.successful(settleUuid)
      case e: AppError => Future.failed(e)
    } recoverWith {
      case e if !e.isInstanceOf[AppError] =>
        logger.error(s"Drop settle on fundUuid($fundUuid)'s settleUuid($settleUuid) failed! Reason: $e")
        Future.failed(AppErrors.UnknownSettleError(e.toString))
    }
  }

  def confirmSettle(fundUuid: String, settleUuid: String): Future[String] = {
    (settleActorRef ? ConfirmSettle(fundUuid, settleUuid)) flatMap {
      case settleUuid: String => Future.successful(settleUuid)
      case e: AppError => Future.failed(e)
    } recoverWith {
      case e if !e.isInstanceOf[AppError] =>
        logger.error(s"Confirm settle on fundUuid($fundUuid)'s settleUuid($settleUuid) failed! Reason: $e")
        Future.failed(AppErrors.UnknownSettleError(e.toString))
    }
  }
}
