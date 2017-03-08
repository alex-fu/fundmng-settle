package com.heqiying.fundmng.settle.domain

import com.heqiying.fundmng.settle.dao._
import com.heqiying.fundmng.settle.model.Trades
import com.heqiying.fundmng.settle.model._
import com.heqiying.fundmng.settle.utils.AppErrors
import EnrichedDividendTypeChanges._
import com.heqiying.fundmng.settle.common.LazyLogging

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._

import scala.collection.mutable

trait TradeAggregator {
  self: TradeChecker =>

  def aggregateTrades(param: SettleParam, settleUuid: String, currShares: Seq[Share], nav: BigDecimal): Future[(Map[String, Share], Seq[Trade])]
}

trait OpenDayTradeAggregator extends TradeAggregator with LazyLogging {
  self: TradeChecker =>

  override def aggregateTrades(param: SettleParam, settleUuid: String, currShares: Seq[Share], nav: BigDecimal): Future[(Map[String, Share], Seq[Trade])] = {
    logger.info(s"Aggregating trades...")
    for {
      (fund, accountMap, redemptions, dividend, dividendTypeChanges, purchases) <- loadTrades(param)
    } yield {
      val (invalidRedemptions, invalidDividendTypeChanges, invalidPurchases) =
        self.checkTrades(fund, accountMap, currShares, nav, redemptions, dividend, dividendTypeChanges, purchases)

      if (invalidRedemptions.nonEmpty || invalidDividendTypeChanges.nonEmpty || invalidPurchases.nonEmpty) {
        import TradeJsonSupport._
        throw AppErrors.InvalidTrades(
          Map(
          "invalidRedemptions" -> invalidRedemptions.toJson,
          "invalidDividendTypeChanges" -> invalidDividendTypeChanges.toJson,
          "invalidPurchases" -> invalidPurchases.toJson
        ).toJson.compactPrint
        )
      } else {
        val newShares = mutable.Map[String, Share]() // Map(accountUuid, newShare) WARN: newShares is used to store current Share state
        // initialize new shares with current shares
        currShares.foreach { s =>
          val newShare = s.copy(id = None, settleUuid = settleUuid, settleDate = new java.sql.Date(System.currentTimeMillis()))
          newShares += (s.accountUuid -> newShare)
        }
        // the settle sequence is `redemption` -> `dividend` -> `purchase`
        val r = redemptionsToTrade(settleUuid, fund, accountMap, newShares, nav, redemptions) ++
          dividendsToTrade(settleUuid, fund, accountMap, newShares, nav - dividend.map(_.perShare).getOrElse(0), dividend, dividendTypeChanges) ++
          purchasesToTrade(settleUuid, fund, accountMap, newShares, nav - dividend.map(_.perShare).getOrElse(0), purchases)
        (newShares.toMap, r)
      }
    }
  }

  private def loadTrades(param: SettleParam) = param match {
    case OpenDaySettleParam(fundUuid, openDate) =>
      val fundF = FundDAO.getReviewed(fundUuid)
      val accountsF = InvestorDAO.getAll
      val redemptionsF = RedemptionDAO.get(fundUuid, openDate)
      val dividendF = DividendDAO.get(fundUuid, openDate)
      val dividendTypeChangesF = DividendTypeChangeDAO.get(fundUuid, openDate)
      val purchasesF = PurchaseDAO.get(fundUuid, openDate)

      for {
        fundO <- fundF
        accounts <- accountsF
        redemptions <- redemptionsF
        dividend <- dividendF
        dividendTypeChanges <- dividendTypeChangesF
        purchases <- purchasesF
      } yield {
        val accountMap = accounts.foldLeft(Map[String, Investor]())((m, a) => m + (a.uuid -> a))
        (fundO.get, accountMap, redemptions, dividend, dividendTypeChanges, purchases)
      }
    case _ => Future.failed(AppErrors.InvalidSettleParam())
  }

  private def redemptionsToTrade(settleUuid: String, f: Fund, am: Map[String, Investor],
    shares: mutable.Map[String, Share], nav: BigDecimal, redemptions: Seq[Redemption]) = {
    def doRedemption(r: Redemption) = {
      val a = am(r.accountUuid)
      val preShare = shares(r.accountUuid)
      val redemAmount: BigDecimal = r.amount.getOrElse(r.share.map(_ * nav).getOrElse(0))
      val redemShare: BigDecimal = redemAmount / nav
      val fee: BigDecimal = 0 // FIXME: fee is undetermined until now
      val settleDate = new java.sql.Date(System.currentTimeMillis())
      val postShare = preShare.copy(share = preShare.share - redemShare, amount = (preShare.share - redemShare) * nav)
      shares.put(r.accountUuid, postShare)

      Trade(None, f.uuid, f.code, f.name, a.uuid, a.account, a.name, a.tpe, "", // TODO: investorIdentityId not fulfilled
        settleUuid, settleDate, r.tradeDate, Trades.RedemptionTradeType, preShare.share, postShare.share,
        nav, 0, 0, redemAmount, 0, 0, 0, 0, fee)
    }
    redemptions.map(doRedemption)
  }

  private def dividendsToTrade(settleUuid: String, f: Fund, am: Map[String, Investor],
    shares: mutable.Map[String, Share], navAfterDividend: BigDecimal,
    dividendO: Option[Dividend], dividendTypeChanges: Seq[DividendTypeChange]) = {
    def doDividend(preShare: Share): Trade = {
      val dividend = dividendO.get
      val a = am(preShare.accountUuid)
      val d = dividendTypeChanges.find(f.uuid, a.uuid)
      val settleDate = new java.sql.Date(System.currentTimeMillis())
      val (dividendCash, dividendReinvestShare): (BigDecimal, BigDecimal) = d.map(_.dividendType) match {
        case Some(DividendTypeChanges.CashDividendType) =>
          (dividend.perShare * preShare.share, 0)
        case Some(DividendTypeChanges.MixDividendType) =>
          val dividendAmount = dividend.perShare * preShare.share
          (dividendAmount * d.get.cashPercent, dividendAmount * (1 - d.get.cashPercent) / navAfterDividend)
        case Some(DividendTypeChanges.ReinvestDividendType) =>
          (0, dividend.perShare * preShare.share / navAfterDividend)
        case None =>
          // FIXME: need to get default dividend method, just use CashDividendType here
          (dividend.perShare * preShare.share, 0)
        case Some(e) =>
          logger.error(s"Do dividend failed! Unknown dividendType $e! use CashDividendType by default")
          (dividend.perShare * preShare.share, 0)
      }

      val postShare =
        preShare.copy(share = preShare.share + dividendReinvestShare, amount = (preShare.share + dividendReinvestShare) * navAfterDividend)
      shares.put(preShare.accountUuid, postShare)

      Trade(None, f.uuid, f.code, f.name, a.uuid, a.account, a.name, a.tpe, "", // TODO: investorIdentityId not fulfilled
        settleUuid, settleDate, dividend.tradeDate, Trades.DividendTradeType, preShare.share, postShare.share,
        navAfterDividend, dividend.perShare, 0, 0, dividendCash, dividendReinvestShare, 0, 0, 0)
    }

    if (dividendO.nonEmpty) {
      shares.toMap.values.map(doDividend)
    } else {
      Seq[Trade]()
    }
  }

  private def purchasesToTrade(settleUuid: String, f: Fund, am: Map[String, Investor],
    shares: mutable.Map[String, Share], navAfterDividend: BigDecimal,
    purchases: Seq[Purchase]) = {
    def doPurchase(p: Purchase) = {
      val a = am(p.accountUuid)
      val preShare = shares.get(p.accountUuid)
      val preShareValue: BigDecimal = preShare.map(_.share).getOrElse(0)
      val purchaseAmount: BigDecimal = p.amount.getOrElse(p.share.map(_ * navAfterDividend).getOrElse(0))
      val purchaseShareValue: BigDecimal = purchaseAmount / navAfterDividend
      val fee: BigDecimal = 0 // FIXME: fee is undetermined until now
      val settleDate = new java.sql.Date(System.currentTimeMillis())
      val postShare = preShare match {
        case Some(s) =>
          s.copy(share = preShareValue + purchaseShareValue, amount = (preShareValue + purchaseShareValue) * navAfterDividend)
        case None => Share(None, f.uuid, f.code, f.name, a.uuid, a.account, a.name, a.tpe, "", // TODO: investorIdentityId not fulfilled
          settleUuid, settleDate, purchaseShareValue, purchaseAmount)
      }
      shares.put(p.accountUuid, postShare)

      Trade(None, f.uuid, f.code, f.name, a.uuid, a.account, a.name, a.tpe, "", // TODO: investorIdentityId not fulfilled
        settleUuid, settleDate, p.tradeDate, Trades.PurchaseTradeType, preShareValue, preShareValue + purchaseShareValue,
        navAfterDividend, 0, purchaseAmount, 0, 0, 0, 0, 0, fee)
    }

    purchases.map(doPurchase)
  }

}