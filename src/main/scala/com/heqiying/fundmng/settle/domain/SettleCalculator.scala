package com.heqiying.fundmng.settle.domain

import com.heqiying.fundmng.settle.common.LazyLogging
import com.heqiying.fundmng.settle.dao.FundDAO
import com.heqiying.fundmng.settle.model._
import com.heqiying.fundmng.settle.utils.AppErrors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SettleCalculator {

  def genNewSettle(settleUuid: String, param: SettleParam): Future[Settle]

  def calcTradeSummaries(settleUuid: String, trades: Seq[Trade]): Seq[TradeSummary]

  def calcInvestStatements(settleUuid: String, newShares: Map[String, Share], trades: Seq[Trade]): Seq[InvestStatement]

  def calcRemits(settleUuid: String, trades: Seq[Trade]): Seq[Remit]
}

trait SettleCalculatorImpl extends SettleCalculator with LazyLogging {
  override def genNewSettle(settleUuid: String, param: SettleParam): Future[Settle] = param match {
    case OpenDaySettleParam(fundUuid, openDate) =>
      val fund = FundDAO.getReviewed(fundUuid)
      fund.map(f => Settle(None, settleUuid, f.get.uuid, f.get.code, f.get.name,
        new java.sql.Date(System.currentTimeMillis()), Settles.OpendaySettleType,
        openDate, Settles.UnsettleState))
    case _ =>
      logger.error(s"generate new settle failed! Invalid settle param $param")
      Future.failed(AppErrors.GenNewSettleFailed("Unsupported SettleParam"))
  }

  override def calcTradeSummaries(settleUuid: String, trades: Seq[Trade]): Seq[TradeSummary] = {
    logger.info(s"Calculating TradeSummaries...")
    if (trades.nonEmpty) {
      trades.groupBy(t => t.tradeType).foldLeft(Seq[Option[TradeSummary]]()) {
        case (s, (tradeType, ts)) =>
          val summary = tradeType match {
            case Trades.RedemptionTradeType =>
              val head = ts.head
              val redemptionAmount = ts.map(_.redemptionAmount).sum
              val redemptionShare = redemptionAmount / head.tradeNav
              val fee = ts.map(_.fee).sum

              Some(
                TradeSummary(None, head.fundUuid, head.fundCode, head.fundName,
                  settleUuid, head.settleDate, tradeType, head.tradeNav,
                  redemptionAmount, redemptionShare, 0, 0, 0, 0, 0,
                  0, 0, fee)
              )

            case Trades.DividendTradeType =>
              val head = ts.head
              val cashDividendAmount = ts.map(_.cashDividendAmount).sum
              val reinvestShare = ts.map(_.reinvestShare).sum

              Some(
                TradeSummary(None, head.fundUuid, head.fundCode, head.fundName,
                  settleUuid, head.settleDate, tradeType, head.tradeNav,
                  0, 0, 0, 0, head.dividendPerShare, cashDividendAmount, reinvestShare,
                  0, 0, 0)
              )

            case Trades.PurchaseTradeType =>
              val head = ts.head
              val purchaseAmount = ts.map(_.purchaseAmount).sum
              val purchaseShare = purchaseAmount / head.tradeNav
              val fee = ts.map(_.fee).sum

              Some(
                TradeSummary(None, head.fundUuid, head.fundCode, head.fundName,
                  settleUuid, head.settleDate, tradeType, head.tradeNav,
                  0, 0, purchaseAmount, purchaseShare, 0, 0, 0,
                  0, 0, fee)
              )

            case _ =>
              logger.info(s"Unknown trade type $tradeType")
              None
          }

          s :+ summary
      }.flatten
    } else {
      Seq[TradeSummary]()
    }
  }

  override def calcInvestStatements(settleUuid: String, newShares: Map[String, Share], trades: Seq[Trade]): Seq[InvestStatement] = {
    logger.info(s"Calculating InvestStatements...")
    Seq[InvestStatement]()
  }

  override def calcRemits(settleUuid: String, trades: Seq[Trade]): Seq[Remit] = {
    logger.info(s"Calculating Remits...")
    // aggregate Trades with `fundUuid + accountUuid`
    val groupedTrades = trades.groupBy(t => (t.fundUuid, t.accountUuid))
    groupedTrades.map {
      case ((fundUuid, accountUuid), ts) =>
        val remitAmount: BigDecimal =
          ts.map(_.redemptionAmount).sum +
            ts.map(_.cashDividendAmount).sum +
            ts.map(_.remitChange).sum

        if (remitAmount > 0) {
          val head = ts.head
          Some(Remit(None, fundUuid, head.fundCode, head.fundName, accountUuid,
            head.account, head.investorName, head.investorType, head.investorIdentityId,
            head.settleUuid, head.settleDate, remitAmount))
        } else {
          None
        }
    }.toSeq.flatten
  }

}
