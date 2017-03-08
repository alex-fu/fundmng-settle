package com.heqiying.fundmng.settle.domain

import com.heqiying.fundmng.settle.common.LazyLogging
import com.heqiying.fundmng.settle.dao._
import com.heqiying.fundmng.settle.model._
import com.heqiying.fundmng.settle.utils.AppErrors

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait SettleRepository {
  def loadCurrentShares(param: SettleParam): Future[Seq[Share]]

  def newSettle(settleUuid: String, settle: Settle, newShares: Map[String, Share], trades: Seq[Trade],
    tradeSummaries: Seq[TradeSummary], statements: Seq[InvestStatement], remits: Seq[Remit]): Future[String]

  def dropSettleResults(settleUuid: String): Future[String]
}

trait SettleRepositoryImpl extends SettleRepository with LazyLogging {

  override def loadCurrentShares(param: SettleParam): Future[Seq[Share]] = param match {
    case OpenDaySettleParam(fundUuid, openDate) =>
      val f = for {
        settle <- SettleDAO.getLatest(fundUuid)
        if settle.nonEmpty
        shares <- ShareDAO.getBySettleUuid(settle.get.uuid)
      } yield shares
      f.recover {
        case e => Seq[Share]()
      }
    case _ =>
      logger.error(s"load current shares failed! Invalid settle param $param")
      Future.failed(AppErrors.InvalidSettleParam())
  }

  override def newSettle(settleUuid: String, settle: Settle, newShares: Map[String, Share], trades: Seq[Trade],
    tradeSummaries: Seq[TradeSummary], statements: Seq[InvestStatement], remits: Seq[Remit]): Future[String] = {
    logger.info(s"Saving new settle...")
    for {
      shareNum <- ShareDAO.insertMany(newShares.values)
      tradeNum <- TradeDAO.insertMany(trades)
      tradeSummaryNum <- TradeSummaryDAO.insertMany(tradeSummaries)
      statementNum <- InvestStatementDAO.insertMany(statements)
      remitNum <- RemitDAO.insertMany(remits)
      _ <- SettleDAO.insert(settle.copy(state = Settles.SettledState))
    } yield {
      logger.info(s"Saved $shareNum shares, $tradeNum trades, $tradeSummaryNum trade summaries, $statementNum invest statements, $remitNum remits on settleUuid $settleUuid")
      settleUuid
    }
  }

  override def dropSettleResults(settleUuid: String) = {
    logger.info(s"Drop unused settle results...")
    for {
      shareNum <- ShareDAO.deleteBySettleUuid(settleUuid)
      tradeNum <- TradeDAO.deleteBySettleUuid(settleUuid)
      tradeSummaryNum <- TradeSummaryDAO.deleteBySettleUuid(settleUuid)
      statementNum <- InvestStatementDAO.deleteBySettleUuid(settleUuid)
      remitNum <- RemitDAO.deleteBySettleUuid(settleUuid)
    } yield {
      logger.info(s"Dropped $shareNum shares, $tradeNum trades, $tradeSummaryNum trade summaries, $statementNum invest statements, $remitNum remits on settleUuid $settleUuid")
      settleUuid
    }
  }
}
