package com.heqiying.fundmng.settle.model

import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import slick.profile.SqlProfile.ColumnOption.Nullable

case class Investor(id: Option[Int], account: String,
  name: String, phone: String, wxid: Option[String], email: Option[String],
  referee: Option[Int], tpe: String, state: String, isActive: Byte,
  uuid: String, version: Int, reviewState: String, isDel: Byte)

object Investors {
  // `type` field
  val PersonType = "person"
  val CompanyType = "company"

  // `state` field
  val RegisteredState = "registered"
  val InvestedState = "invested"

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

class InvestorTable(tag: Tag) extends Table[Investor](tag, "investors") {
  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  def account = column[String]("account", O.Length(127))
  def name = column[String]("name", O.Length(127))
  def phone = column[String]("phone", O.Length(31))
  def wxid = column[String]("wxid", O.Length(127), Nullable)
  def email = column[String]("email", O.Length(127), Nullable)
  def referee = column[Int]("referee", Nullable)
  def tpe = column[String]("type", O.Length(63))
  def state = column[String]("state", O.Length(63))
  def isActive = column[Byte]("is_active")
  def uuid = column[String]("uuid", O.Length(63, varying = false))
  def version = column[Int]("version")
  def reviewState = column[String]("review_state", O.Length(63))
  def isDel = column[Byte]("is_del")

  def * = (id.?, account, name, phone, wxid.?, email.?, referee.?, tpe, state, isActive, uuid, version, reviewState, isDel) <>
    (Investor.tupled, Investor.unapply)
}
