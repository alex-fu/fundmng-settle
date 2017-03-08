package com.heqiying.fundmng.settle.domain

import com.heqiying.fundmng.settle.domain.EnrichedRedemptions._
import com.heqiying.fundmng.settle.domain.EnrichedShares._
import com.heqiying.fundmng.settle.domain.EnrichedPurchases._
import com.heqiying.fundmng.settle.domain.EnrichedDividendTypeChanges._
import com.heqiying.fundmng.settle.model._

trait TradeChecker {
  // return invalid Trades
  def checkTrades(fund: Fund, accountMap: Map[String, Investor],
    currShares: Seq[Share], nav: BigDecimal,
    redemptions: Seq[Redemption], dividend: Option[Dividend],
    dividendTypeChanges: Seq[DividendTypeChange],
    purchases: Seq[Purchase]): (Seq[Redemption], Seq[DividendTypeChange], Seq[Purchase])
}

trait TradeCheckerImpl extends TradeChecker {
  private val leastInvestAmount: BigDecimal = 1000000

  override def checkTrades(fund: Fund, accountMap: Map[String, Investor],
    currShares: Seq[Share], nav: BigDecimal,
    redemptions: Seq[Redemption], dividend: Option[Dividend],
    dividendTypeChanges: Seq[DividendTypeChange],
    purchases: Seq[Purchase]): (Seq[Redemption], Seq[DividendTypeChange], Seq[Purchase]) = {
    (
      checkRedemption(fund, accountMap, currShares, nav, redemptions),
      checkDividend(fund, accountMap, currShares, nav, redemptions, dividend, dividendTypeChanges),
      checkPurchase(fund, accountMap, currShares, nav, purchases)
    )
  }

  private[this] def checkRedemption(fund: Fund, accountMap: Map[String, Investor],
    currShares: Seq[Share], nav: BigDecimal,
    redemptions: Seq[Redemption]): Seq[Redemption] = {

    def isInvalid(redem: Redemption): Boolean = {
      fund.uuid != redem.fundUuid ||
        accountMap.get(redem.accountUuid).isEmpty ||
        currShares.find(redem.fundUuid, redem.accountUuid).isEmpty
    }

    def notUnique(redem: Redemption): Boolean = {
      redemptions.findAll(redem.fundUuid, redem.accountUuid).length != 1
    }

    def notFulfilled(redem: Redemption): Boolean = {
      currShares.find(redem.fundUuid, redem.accountUuid) match {
        case Some(share) =>
          val holdAmount: BigDecimal = share.share * nav
          val redemCash: BigDecimal = redem.amount.getOrElse(redem.share.map(_ * nav).getOrElse(0))
          holdAmount - redemCash < leastInvestAmount
        case None => true
      }
    }

    redemptions.filter(x => isInvalid(x) || notUnique(x) || notFulfilled(x))
  }

  private[this] def checkDividend(fund: Fund, accountMap: Map[String, Investor],
    currShares: Seq[Share], nav: BigDecimal, redemptions: Seq[Redemption],
    dividend: Option[Dividend], dividendTypeChanges: Seq[DividendTypeChange]): Seq[DividendTypeChange] = {

    def isInvalid(dtc: DividendTypeChange): Boolean = {
      fund.uuid != dtc.fundUuid ||
        accountMap.get(dtc.accountUuid).isEmpty ||
        currShares.find(dtc.fundUuid, dtc.accountUuid).isEmpty
    }

    def notUnique(dtc: DividendTypeChange): Boolean = {
      dividendTypeChanges.findAll(dtc.fundUuid, dtc.accountUuid).length != 1
    }

    def notFulfilled(dtc: DividendTypeChange): Boolean = {
      val share = currShares.find(dtc.fundUuid, dtc.accountUuid)
      val holdAmount: BigDecimal = share.map(_.share * nav).getOrElse(0)
      val redem = redemptions.find(dtc.fundUuid, dtc.accountUuid)
      val redemCash: BigDecimal = redem.flatMap(_.amount).getOrElse(redem.flatMap(_.share).map(_ * nav).getOrElse(0))
      val dividendCash: BigDecimal =
        if (dividend.nonEmpty) {
          dtc.dividendType match {
            case DividendTypeChanges.CashDividendType => dividend.get.perShare * share.map(_.share).getOrElse(0)
            case DividendTypeChanges.MixDividendType => dividend.get.perShare * share.map(_.share).getOrElse(0) * dtc.cashPercent
            case _ => 0
          }
        } else 0
      holdAmount - redemCash - dividendCash < leastInvestAmount
    }

    dividendTypeChanges.filter(x => isInvalid(x) || notUnique(x) || notFulfilled(x))
  }

  private[this] def checkPurchase(fund: Fund, accountMap: Map[String, Investor],
    currShares: Seq[Share], nav: BigDecimal,
    purchases: Seq[Purchase]): Seq[Purchase] = {

    def isInvalid(purchase: Purchase): Boolean = {
      fund.uuid != purchase.fundUuid ||
        accountMap.get(purchase.accountUuid).isEmpty
    }

    def notUnique(purchase: Purchase): Boolean = {
      purchases.findAll(purchase.fundUuid, purchase.accountUuid).length != 1
    }

    def notFulfilled(purchase: Purchase): Boolean = {
      val purchaseAmount: BigDecimal = purchase.amount.getOrElse(purchase.share.map(_ * nav).getOrElse(0))
      val share = currShares.find(purchase.fundUuid, purchase.accountUuid)
      val holdAmount: BigDecimal = share.map(_.share * nav).getOrElse(0)
      holdAmount + purchaseAmount < leastInvestAmount
    }

    purchases.filter(x => isInvalid(x) || notUnique(x) || notFulfilled(x))
  }
}