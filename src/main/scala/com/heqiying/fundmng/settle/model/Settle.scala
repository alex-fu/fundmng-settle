package com.heqiying.fundmng.settle.model

import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import java.sql.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import shapeless._
import slickless._
import spray.json.{ DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, RootJsonFormat }

case class Settle(id: Option[Int], uuid: String, fundUuid: String, fundCode: String,
  fundName: String, settleDate: Date, tpe: String,
  openDate: Date, state: String) extends Model[Settle]

object Settles {
  val OpendaySettleType = "openday"
  val ShareChangeSettleType = "share_change"
  val ShareConversionSettleType = "share_conversion"

  val UnsettleState = "unsettle"
  val SettledState = "settled"
  val ConfirmedState = "confirmed"
  val DroppedState = "dropped"
}

class SettleTable(tag: Tag) extends Table[Settle](tag, "settles") {
  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def uuid = column[String]("uuid", O.Length(63, varying = false))
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def fundName = column[String]("fund_name", O.Length(127))
  def settleDate = column[Date]("settle_date")
  def tpe = column[String]("type", O.Length(63))
  def openDate = column[Date]("open_date")
  def state = column[String]("state", O.Length(63))

  def idxFund = index("idx_fund", fundUuid, unique = false)

  def * = (id.?, uuid, fundUuid, fundCode, fundName, settleDate, tpe, openDate, state) <> (Settle.tupled, Settle.unapply)
}

case class Share(id: Option[Long], fundUuid: String, fundCode: String,
  fundName: String, accountUuid: String, account: String,
  investorName: String, investorType: String, investorIdentityId: String,
  settleUuid: String, settleDate: Date, share: BigDecimal, amount: BigDecimal) extends Model[Share]

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
  settleUuid: String, settleDate: Date, tradeDate: Date, tradeType: String,
  preShare: BigDecimal, share: BigDecimal, tradeNav: BigDecimal,
  dividendPerShare: BigDecimal, purchaseAmount: BigDecimal, redemptionAmount: BigDecimal,
  cashDividendAmount: BigDecimal, reinvestShare: BigDecimal,
  shareChange: BigDecimal, remitChange: BigDecimal, fee: BigDecimal) extends Model[Trade]

object Trades {
  val RedemptionTradeType = "redemption"
  val DividendTradeType = "dividend"
  val PurchaseTradeType = "purchase"
  val SubscribeTradeType = "subscribe"
  val ChangeShareTradeType = "changeShare"
  val ConversionTradeType = "conversion"
}

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
  def settleDate = column[Date]("settle_date")
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
  def shareChange = column[BigDecimal]("share_change")
  def remitChange = column[BigDecimal]("remit_change")
  def fee = column[BigDecimal]("fee")

  def uniqSFAT = index("uniq_settle_fund_account_type", (settleUuid, fundUuid, accountUuid, tradeType), unique = true)

  def * = (id.? :: fundUuid :: fundCode :: fundName :: accountUuid :: account :: investorName ::
    investorType :: investorIdentityId :: settleUuid :: settleDate :: tradeDate :: tradeType :: preShare ::
    share :: tradeNav :: dividendPerShare :: purchaseAmount :: redemptionAmount :: cashDividendAmount ::
    reinvestShare :: shareChange :: remitChange :: fee :: HNil).mappedWith(Generic[Trade])
}

case class TradeSummary(id: Option[Long], fundUuid: String, fundCode: String, fundName: String,
  settleUuid: String, settleDate: Date, tradeType: String, tradeNav: BigDecimal,
  redemptionAmount: BigDecimal, redemptionShare: BigDecimal,
  purchaseAmount: BigDecimal, purchaseShare: BigDecimal,
  dividendPerShare: BigDecimal, cashDividendAmount: BigDecimal, reinvestShare: BigDecimal,
  shareChange: BigDecimal, remitChange: BigDecimal, fee: BigDecimal) extends Model[TradeSummary]

class TradeSummaryTable(tag: Tag) extends Table[TradeSummary](tag, "trade_summaries") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def fundName = column[String]("fund_name", O.Length(127))
  def settleUuid = column[String]("settle_uuid", O.Length(63, varying = false))
  def settleDate = column[Date]("settle_date")
  def tradeType = column[String]("trade_type", O.Length(63))
  def tradeNav = column[BigDecimal]("trade_nav")
  def redemptionAmount = column[BigDecimal]("redemption_amount")
  def redemptionShare = column[BigDecimal]("redemption_share")
  def purchaseAmount = column[BigDecimal]("purchase_amount")
  def purchaseShare = column[BigDecimal]("purchase_share")
  def dividendPerShare = column[BigDecimal]("dividend_per_share")
  def cashDividendAmount = column[BigDecimal]("cash_dividend_amount")
  def reinvestShare = column[BigDecimal]("reinvest_share")
  def shareChange = column[BigDecimal]("share_change")
  def remitChange = column[BigDecimal]("remit_change")
  def fee = column[BigDecimal]("fee")

  def uniqSF = index("uniq_settle_fund_type", (settleUuid, fundUuid, tradeType), unique = true)

  def * = (id.?, fundUuid, fundCode, fundName, settleUuid, settleDate, tradeType, tradeNav,
    redemptionAmount, redemptionShare, purchaseAmount, purchaseShare, dividendPerShare, cashDividendAmount,
    reinvestShare, shareChange, remitChange, fee) <> (TradeSummary.tupled, TradeSummary.unapply)
}

case class InvestStatement(id: Option[Long], fundUuid: String, fundCode: String,
  fundName: String, accountUuid: String, account: String,
  investorName: String, investorType: String, investorIdentityId: String,
  settleUuid: String, settleDate: Date, tradeType: String,
  statementFileUuid: String, state: String) extends Model[InvestStatement]

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
  def settleDate = column[Date]("settle_date")
  def tradeType = column[String]("trade_type", O.Length(63))
  def statementFileUuid = column[String]("statementFileUuid", O.Length(63, varying = false))
  def state = column[String]("state", O.Length(63))

  def uniqSFA = index("uniq_settle_fund_account", (settleUuid, fundUuid, accountUuid), unique = true)

  def * = (id.?, fundUuid, fundCode, fundName, accountUuid, account, investorName,
    investorType, investorIdentityId, settleUuid, settleDate, tradeType, statementFileUuid,
    state) <> (InvestStatement.tupled, InvestStatement.unapply)
}

case class Remit(id: Option[Long], fundUuid: String, fundCode: String,
  fundName: String, accountUuid: String, account: String,
  investorName: String, investorType: String, investorIdentityId: String,
  settleUuid: String, settleDate: Date, remitAmount: BigDecimal) extends Model[Remit]

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
  def settleDate = column[Date]("settle_date")
  def remitAmount = column[BigDecimal]("remit_amount")

  def uniqSFA = index("uniq_settle_fund_account", (settleUuid, fundUuid, accountUuid), unique = true)

  def * = (id.?, fundUuid, fundCode, fundName, accountUuid, account, investorName,
    investorType, investorIdentityId, settleUuid, settleDate, remitAmount) <> (Remit.tupled, Remit.unapply)
}

object SettleJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object sqlDateJsonFormat extends RootJsonFormat[java.sql.Date] {
    override def read(json: JsValue): Date = Date.valueOf(json.toString())
    override def write(obj: Date): JsValue = JsString(obj.toString)
  }

  implicit val settleJsonFormat = jsonFormat9(Settle.apply)
  implicit val shareJsonFormat = jsonFormat13(Share.apply)
  implicit val tradeSummaryJsonFormat = jsonFormat18(TradeSummary.apply)
  implicit val investStatementJsonFormat = jsonFormat14(InvestStatement.apply)
  implicit val remitJsonFormat = jsonFormat12(Remit.apply)

  implicit object tradeJsonSupport extends RootJsonFormat[Trade] {
    def write(p: Trade) = {
      val fields = new collection.mutable.ListBuffer[(String, JsValue)]
      fields.sizeHint(24 * 25)
      fields ++= productElement2Field[Option[Long]]("id", p, 0)
      fields ++= productElement2Field[String]("fundUuid", p, 1)
      fields ++= productElement2Field[String]("fundCode", p, 2)
      fields ++= productElement2Field[String]("fundName", p, 3)
      fields ++= productElement2Field[String]("accountUuid", p, 4)
      fields ++= productElement2Field[String]("account", p, 5)
      fields ++= productElement2Field[String]("investorName", p, 6)
      fields ++= productElement2Field[String]("investorType", p, 7)
      fields ++= productElement2Field[String]("investorIdentityId", p, 8)
      fields ++= productElement2Field[String]("settleUuid", p, 9)
      fields ++= productElement2Field[Date]("settleDate", p, 10)
      fields ++= productElement2Field[Date]("tradeDate", p, 11)
      fields ++= productElement2Field[String]("tradeType", p, 12)
      fields ++= productElement2Field[BigDecimal]("preShare", p, 13)
      fields ++= productElement2Field[BigDecimal]("share", p, 14)
      fields ++= productElement2Field[BigDecimal]("tradeNav", p, 15)
      fields ++= productElement2Field[BigDecimal]("dividendPerShare", p, 16)
      fields ++= productElement2Field[BigDecimal]("purchaseAmount", p, 17)
      fields ++= productElement2Field[BigDecimal]("redemptionAmount", p, 18)
      fields ++= productElement2Field[BigDecimal]("cashDividendAmount", p, 19)
      fields ++= productElement2Field[BigDecimal]("reinvestShare", p, 20)
      fields ++= productElement2Field[BigDecimal]("shareChange", p, 21)
      fields ++= productElement2Field[BigDecimal]("remitChange", p, 22)
      fields ++= productElement2Field[BigDecimal]("fee", p, 23)
      JsObject(fields: _*)
    }
    def read(j: JsValue) = ??? // no need to implement
  }
}
