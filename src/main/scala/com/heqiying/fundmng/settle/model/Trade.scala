package com.heqiying.fundmng.settle.model

import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import java.sql.Date

import slick.profile.SqlProfile.ColumnOption.Nullable

case class Purchase(id: Option[Long], accountUuid: String, account: String,
  fundUuid: String, fundCode: String,
  openDate: Date, tpe: String, tradeDate: Date, fee: Option[BigDecimal],
  share: Option[BigDecimal], amount: Option[BigDecimal], comment: Option[String])

object Purchases {
  val SubscribeType = "subscribe"
  val PurchaseType = "purchase"
}

class PurchaseTable(tag: Tag) extends Table[Purchase](tag, "purchases") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def accountUuid = column[String]("account_uuid", O.Length(63, varying = false))
  def account = column[String]("account", O.Length(127))
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def openDate = column[Date]("open_date")
  def tpe = column[String]("type", O.Length(63))
  def tradeDate = column[Date]("trade_date")
  def fee = column[BigDecimal]("fee", Nullable)
  def share = column[BigDecimal]("share", Nullable)
  def amount = column[BigDecimal]("amount", Nullable)
  def comment = column[String]("comment", Nullable)

  def * = (id.?, accountUuid, account, fundUuid, fundCode, openDate, tpe,
    tradeDate, fee.?, share.?, amount.?, comment.?) <> (Purchase.tupled, Purchase.unapply)
}

case class Redemption(id: Option[Long], accountUuid: String, account: String,
  fundUuid: String, fundCode: String,
  openDate: Date, tradeDate: Date, fee: Option[BigDecimal],
  share: Option[BigDecimal], amount: Option[BigDecimal], comment: Option[String])

class RedemptionTable(tag: Tag) extends Table[Redemption](tag, "redemptions") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def accountUuid = column[String]("account_uuid", O.Length(63, varying = false))
  def account = column[String]("account", O.Length(127))
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def openDate = column[Date]("open_date")
  def tradeDate = column[Date]("trade_date")
  def fee = column[BigDecimal]("fee", Nullable)
  def share = column[BigDecimal]("share", Nullable)
  def amount = column[BigDecimal]("amount", Nullable)
  def comment = column[String]("comment", Nullable)

  def * = (id.?, accountUuid, account, fundUuid, fundCode, openDate, tradeDate,
    fee.?, share.?, amount.?, comment.?) <> (Redemption.tupled, Redemption.unapply)
}

case class DividendTypeChange(id: Option[Long], accountUuid: String, account: String,
  fundUuid: String, fundCode: String,
  openDate: Date, tradeDate: Date, dividendType: String,
  cashPercent: BigDecimal, comment: Option[String])

class DividendTypeChangeTable(tag: Tag) extends Table[DividendTypeChange](tag, "dividend_type_changes") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def accountUuid = column[String]("account_uuid", O.Length(63, varying = false))
  def account = column[String]("account", O.Length(127))
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def openDate = column[Date]("open_date")
  def tradeDate = column[Date]("trade_date")
  def dividendType = column[String]("dividend_type", O.Length(63))
  def cashPercent = column[BigDecimal]("cash_percent")
  def comment = column[String]("comment", Nullable)

  def * = (id.?, accountUuid, account, fundUuid, fundCode, openDate, tradeDate,
    dividendType, cashPercent, comment.?) <> (DividendTypeChange.tupled, DividendTypeChange.unapply)
}

case class Dividend(id: Option[Long], fundUuid: String, fundCode: String,
  openDate: Date, tradeDate: Date, perShare: BigDecimal, comment: Option[String])

class DividendTable(tag: Tag) extends Table[Dividend](tag, "dividends") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def openDate = column[Date]("open_date")
  def tradeDate = column[Date]("trade_date")
  def perShare = column[BigDecimal]("per_share")
  def comment = column[String]("comment", Nullable)

  def * = (id.?, fundUuid, fundCode, openDate, tradeDate, perShare, comment.?) <> (Dividend.tupled, Dividend.unapply)
}
