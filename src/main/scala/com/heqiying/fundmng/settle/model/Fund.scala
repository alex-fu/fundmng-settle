package com.heqiying.fundmng.settle.model

import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import slick.profile.SqlProfile.ColumnOption.Nullable
import java.sql.Date

case class Fund(id: Option[Int], code: String, name: String, shortName: Option[String],
  openDuration: Option[String], createDate: Option[Date], terminateDate: Option[Date],
  subscribeFeeRate: Option[BigDecimal], purchaseFeeRate: Option[BigDecimal],
  redemptionFeeRate: Option[BigDecimal], rewardRate: Option[BigDecimal],
  navPrecision: Option[Int], sharePrecision: Option[Int], amountPrecision: Option[Int],
  fundState: String, inChargeInputer: Option[String], inChargeReviewer: Option[String],
  uuid: String, version: Int, reviewState: String, isDel: Byte)

object Funds {
  // `type` field
  val RaisingFundState = "raising"
  val OpeningFundState = "opening"
  val ClosingFundState = "closing"
  val TerminatedFundState = "terminated"

  // `reviewState` field
  val UnsubmitReviewState = "unsubmit"
  val UnreviewedReviewState = "unreviewed"
  val ReviewedReviewState = "reviewed"
  val CanceledReviewState = "canceled"

  // `Active` field
  val Active: Byte = 1
  val Inactive: Byte = 0

  // `isDel` field
  val Deleted: Byte = 1
  val Alive: Byte = 0
}

class FundTable(tag: Tag) extends Table[Fund](tag, "funds") {
  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def code = column[String]("code", O.Length(100))
  def name = column[String]("name", O.Length(100))
  def shortName = column[String]("short_name", O.Length(100), Nullable)
  def openDuration = column[String]("duration", O.Length(100), Nullable)
  def createDate = column[Date]("create_day", Nullable)
  def terminateDate = column[Date]("terminate_day", Nullable)
  def subscribeFeeRate = column[BigDecimal]("subscribe_fee_rate", Nullable)
  def purchaseFeeRate = column[BigDecimal]("purchase_fee_rate", Nullable)
  def redemptionFeeRate = column[BigDecimal]("redemption_fee_rate", Nullable)
  def rewardRate = column[BigDecimal]("revenue_distribution", Nullable)
  def navPrecision = column[Int]("npv_precision", Nullable)
  def sharePrecision = column[Int]("shares_precision", Nullable)
  def amountPrecision = column[Int]("amount_precision", Nullable)
  def fundState = column[String]("status", Nullable)
  def inChargeInputer = column[String]("inChargeInputer", Nullable)
  def inChargeReviewer = column[String]("inChargeReviewer", Nullable)
  def uuid = column[String]("mark")
  def version = column[Int]("version")
  def reviewState = column[String]("review_state")
  def isDel = column[Byte]("is_del")

  def * = (id.?, code, name, shortName.?, openDuration.?, createDate.?, terminateDate.?,
    subscribeFeeRate.?, purchaseFeeRate.?, redemptionFeeRate.?, rewardRate.?,
    navPrecision.?, sharePrecision.?, amountPrecision.?, fundState, inChargeInputer.?,
    inChargeReviewer.?, uuid, version, reviewState, isDel) <> (Fund.tupled, Fund.unapply)
}

case class FundNav(id: Option[Int], fundUuid: String, fundCode: String, date: Date,
  beforeRewardNav: BigDecimal, nav: BigDecimal, accumulativeNav: BigDecimal)

class FundNavTable(tag: Tag) extends Table[FundNav](tag, "fund_navs") {
  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def fundUuid = column[String]("fund_uuid", O.Length(63, varying = false))
  def fundCode = column[String]("fund_code", O.Length(127))
  def date = column[Date]("date")
  def beforeRewardNav = column[BigDecimal]("before_reward_nav")
  def nav = column[BigDecimal]("nav")
  def accumulativeNav = column[BigDecimal]("accumulative_nav")

  def * = (id.?, fundUuid, fundCode, date, beforeRewardNav, nav, accumulativeNav) <> (FundNav.tupled, FundNav.unapply)
}
