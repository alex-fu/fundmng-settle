package com.heqiying.fundmng.settle.model

import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._

import java.sql.Date

case class Settle(uuid: String, fundUuid: String, fundCode: String,
  fundName: String, settleDate: Date, tpe: String,
  openDate: Date, state: String)

class SettleTable(tag: Tag) extends Table[Settle](tag, "settles") {
  def uuid = column[String]("uuid", O.Length(63, varying = false), O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def fundName = column[String]("fund_name", O.Length(127))
  def settleDate = column[Date]("settle_date")
  def tpe = column[String]("type", O.Length(63))
  def openDate = column[Date]("open_date")
  def state = column[String]("state", O.Length(63))

  def idxFund = index("idx_fund", fundUuid, unique = false)

  def * = (uuid, fundUuid, fundCode, fundName, settleDate, tpe, openDate, state) <> (Settle.tupled, Settle.unapply)
}

case class Share(id: Option[Long], fundUuid: String, fundCode: String,
  fundName: String, accountUuid: String, account: String,
  investorName: String, investorType: String, investorIdentityId: String,
  settleUuid: String, settleDate: Date, share: BigDecimal, amount: BigDecimal)

class ShareTable(tag: Tag) extends Table[Share](tag, "shares") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def fundName = column[String]("fund_name", O.Length(127))
  def accountUuid = column[String]("account_uuid", O.Length(63, varying = false))
  def account = column[String]("account", O.Length(127))
  def investorName = column[String]("investor_name", O.Length(127))
  def investorType = column[String]("investor_type", O.Length(63))
  def investorIdentityId = column[String]("investor_identity_id", O.Length(127))
  def settleUuid = column[String]("settle_uuid", O.Length(63, varying = false))
  def settleDate = column[Date]("settle_date")
  def share = column[BigDecimal]("share")
  def amount = column[BigDecimal]("amount")

  def uniqSFA = index("uniq_settle_fund_account", (settleUuid, fundUuid, accountUuid), unique = true)

  def * = (id.?, fundUuid, fundCode, fundName, accountUuid, account, investorName,
    investorType, investorIdentityId, settleUuid, settleDate, share, amount) <> (Share.tupled, Share.unapply)
}

case class Trade(id: Option[Long], fundUuid: String, fundCode: String,
  fundName: String, accountUuid: String, account: String,
  investorName: String, investorType: String, investorIdentityId: String,
  settleUuid: String, tradeDate: Date, tradeType: String,
  preShare: BigDecimal, share: BigDecimal, tradeNav: BigDecimal,
  dividendPerShare: BigDecimal, purchaseAmount: BigDecimal, redemptionAmount: BigDecimal,
  cashDividendAmount: BigDecimal, reinvestShare: BigDecimal, fee: BigDecimal)

class TradeTable(tag: Tag) extends Table[Trade](tag, "trades") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def fundName = column[String]("fund_name", O.Length(127))
  def accountUuid = column[String]("account_uuid", O.Length(63, varying = false))
  def account = column[String]("account", O.Length(127))
  def investorName = column[String]("investor_name", O.Length(127))
  def investorType = column[String]("investor_type", O.Length(63))
  def investorIdentityId = column[String]("investor_identity_id", O.Length(127))
  def settleUuid = column[String]("settle_uuid", O.Length(63, varying = false))
  def tradeDate = column[Date]("trade_date")
  def tradeType = column[String]("trade_type", O.Length(63))
  def preShare = column[BigDecimal]("pre_share")
  def share = column[BigDecimal]("share")
  def tradeNav = column[BigDecimal]("trade_nav")
  def dividendPerShare = column[BigDecimal]("dividend_per_share")
  def purchaseAmount = column[BigDecimal]("purchase_amount")
  def redemptionAmount = column[BigDecimal]("redemption_amount")
  def cashDividendAmount = column[BigDecimal]("cash_dividend_amount")
  def reinvestShare = column[BigDecimal]("reinvest_share")
  def fee = column[BigDecimal]("fee")

  def uniqSFAT = index("uniq_settle_fund_account_type", (settleUuid, fundUuid, accountUuid, tradeType), unique = true)

  def * = (id.?, fundUuid, fundCode, fundName, accountUuid, account, investorName,
    investorType, investorIdentityId, settleUuid, tradeDate, tradeType, preShare,
    share, tradeNav, dividendPerShare, purchaseAmount, redemptionAmount, cashDividendAmount,
    reinvestShare, fee) <> (Trade.tupled, Trade.unapply)
}

case class TradeSummary(id: Option[Long], fundUuid: String, fundCode: String, fundName: String,
  settleUuid: String, tradeDate: Date, tradeType: String,
  preShare: BigDecimal, share: BigDecimal, tradeNav: BigDecimal,
  dividendPerShare: BigDecimal, purchaseAmount: BigDecimal, redemptionAmount: BigDecimal,
  cashDividendAmount: BigDecimal, reinvestShare: BigDecimal, fee: BigDecimal)

class TradeSummaryTable(tag: Tag) extends Table[TradeSummary](tag, "trade_summaries") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def fundName = column[String]("fund_name", O.Length(127))
  def settleUuid = column[String]("settle_uuid", O.Length(63, varying = false))
  def tradeDate = column[Date]("trade_date")
  def tradeType = column[String]("trade_type", O.Length(63))
  def preShare = column[BigDecimal]("pre_share")
  def share = column[BigDecimal]("share")
  def tradeNav = column[BigDecimal]("trade_nav")
  def dividendPerShare = column[BigDecimal]("dividend_per_share")
  def purchaseAmount = column[BigDecimal]("purchase_amount")
  def redemptionAmount = column[BigDecimal]("redemption_amount")
  def cashDividendAmount = column[BigDecimal]("cash_dividend_amount")
  def reinvestShare = column[BigDecimal]("reinvest_share")
  def fee = column[BigDecimal]("fee")

  def uniqSF = index("uniq_settle_fund", (settleUuid, fundUuid), unique = true)

  def * = (id.?, fundUuid, fundCode, fundName, settleUuid, tradeDate, tradeType, preShare,
    share, tradeNav, dividendPerShare, purchaseAmount, redemptionAmount, cashDividendAmount,
    reinvestShare, fee) <> (TradeSummary.tupled, TradeSummary.unapply)
}

case class InvestStatement(id: Option[Long], fundUuid: String, fundCode: String,
  fundName: String, accountUuid: String, account: String,
  investorName: String, investorType: String, investorIdentityId: String,
  settleUuid: String, tradeDate: Date, tradeType: String,
  statementFileUuid: String, state: String)

class InvestStatementTable(tag: Tag) extends Table[InvestStatement](tag, "invest_statements") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def fundName = column[String]("fund_name", O.Length(127))
  def accountUuid = column[String]("account_uuid", O.Length(63, varying = false))
  def account = column[String]("account", O.Length(127))
  def investorName = column[String]("investor_name", O.Length(127))
  def investorType = column[String]("investor_type", O.Length(63))
  def investorIdentityId = column[String]("investor_identity_id", O.Length(127))
  def settleUuid = column[String]("settle_uuid", O.Length(63, varying = false))
  def tradeDate = column[Date]("trade_date")
  def tradeType = column[String]("trade_type", O.Length(63))
  def statementFileUuid = column[String]("statementFileUuid", O.Length(63, varying = false))
  def state = column[String]("state", O.Length(63))

  def uniqSFA = index("uniq_settle_fund_account", (settleUuid, fundUuid, accountUuid), unique = true)

  def * = (id.?, fundUuid, fundCode, fundName, accountUuid, account, investorName,
    investorType, investorIdentityId, settleUuid, tradeDate, tradeType, statementFileUuid,
    state) <> (InvestStatement.tupled, InvestStatement.unapply)
}

case class Remit(id: Option[Long], fundUuid: String, fundCode: String,
  fundName: String, accountUuid: String, account: String,
  investorName: String, investorType: String, investorIdentityId: String,
  settleUuid: String, tradeDate: Date, remitAmount: BigDecimal)

class RemitTable(tag: Tag) extends Table[Remit](tag, "remits") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def fundName = column[String]("fund_name", O.Length(127))
  def accountUuid = column[String]("account_uuid", O.Length(63, varying = false))
  def account = column[String]("account", O.Length(127))
  def investorName = column[String]("investor_name", O.Length(127))
  def investorType = column[String]("investor_type", O.Length(63))
  def investorIdentityId = column[String]("investor_identity_id", O.Length(127))
  def settleUuid = column[String]("settle_uuid", O.Length(63, varying = false))
  def tradeDate = column[Date]("trade_date")
  def remitAmount = column[BigDecimal]("remit_amount")

  def uniqSFA = index("uniq_settle_fund_account", (settleUuid, fundUuid, accountUuid), unique = true)

  def * = (id.?, fundUuid, fundCode, fundName, accountUuid, account, investorName,
    investorType, investorIdentityId, settleUuid, tradeDate, remitAmount) <> (Remit.tupled, Remit.unapply)
}
