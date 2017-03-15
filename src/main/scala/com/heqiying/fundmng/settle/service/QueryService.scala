package com.heqiying.fundmng.settle.service

import java.sql.Date

import com.heqiying.fundmng.settle.common.LazyLogging
import com.heqiying.fundmng.settle.dao._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.model._
import com.heqiying.fundmng.settle.utils.AppErrors

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success, Try }

trait QueryService extends LazyLogging {
  def getShares(settleUuid: String): Future[Seq[Share]] = {
    val f = for {
      settle <- SettleDAO.getOne(settleUuid)
      if settle.nonEmpty && (settle.get.state == Settles.SettledState || settle.get.state == Settles.ConfirmedState)
      shares <- ShareDAO.getBySettleUuid(settleUuid)
    } yield shares
    f.recover {
      case e =>
        logger.error(s"Get shares failed! Reason: $e")
        Seq[Share]()
    }
  }

  def getTrades(settleUuid: String): Future[Seq[Trade]] = {
    val f = for {
      settle <- SettleDAO.getOne(settleUuid)
      if settle.nonEmpty && (settle.get.state == Settles.SettledState || settle.get.state == Settles.ConfirmedState)
      trades <- TradeDAO.getBySettleUuid(settleUuid)
    } yield trades
    f.recover {
      case e =>
        logger.error(s"Get trades failed! Reason: $e")
        Seq[Trade]()
    }
  }

  def getTradeSummaries(settleUuid: String): Future[Seq[TradeSummary]] = {
    val f = for {
      settle <- SettleDAO.getOne(settleUuid)
      if settle.nonEmpty && (settle.get.state == Settles.SettledState || settle.get.state == Settles.ConfirmedState)
      tradeSummaries <- TradeSummaryDAO.getBySettleUuid(settleUuid)
    } yield tradeSummaries
    f.recover {
      case e =>
        logger.error(s"Get trade summaries failed! Reason: $e")
        Seq[TradeSummary]()
    }
  }

  def getInvestStatements(settleUuid: String): Future[Seq[InvestStatement]] = {
    val f = for {
      settle <- SettleDAO.getOne(settleUuid)
      if settle.nonEmpty && (settle.get.state == Settles.SettledState || settle.get.state == Settles.ConfirmedState)
      statements <- InvestStatementDAO.getBySettleUuid(settleUuid)
    } yield statements
    f.recover {
      case e =>
        logger.error(s"Get invest statements failed! Reason: $e")
        Seq[InvestStatement]()
    }
  }

  def getRemits(settleUuid: String): Future[Seq[Remit]] = {
    val f = for {
      settle <- SettleDAO.getOne(settleUuid)
      if settle.nonEmpty && (settle.get.state == Settles.SettledState || settle.get.state == Settles.ConfirmedState)
      remits <- RemitDAO.getBySettleUuid(settleUuid)
    } yield remits
    f.recover {
      case e =>
        logger.error(s"Get remits failed! Reason: $e")
        Seq[Remit]()
    }
  }

  def getSettles(fundUuid: String) = {
    val f = SettleDAO.getAllByFund(fundUuid)
    f.recover {
      case e =>
        logger.error(s"Get settle histories for $fundUuid failed! Reason: $e")
        Seq[Settle]()
    }
  }

  def getSettle(fundUuid: String, openDate: String): Future[Option[Settle]] = {
    Try(Date.valueOf(openDate)) match {
      case Success(d) =>
        SettleDAO.get(fundUuid, d).recover {
          case e =>
            logger.error(s"Get settle for $fundUuid($openDate) failed! Reason: $e")
            None
        }
      case Failure(e) =>
        logger.error(s"Invalid open date $openDate on fund $fundUuid")
        Future.failed(AppErrors.InvalidOpenDay())
    }
  }
}
