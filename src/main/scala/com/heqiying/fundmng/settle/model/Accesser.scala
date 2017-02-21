package com.heqiying.fundmng.settle.model

case class Accesser(loginName: String, name: Option[String], email: Option[String], wxid: Option[String], groupType: String)

object GroupType {
  val GroupTypeAdmin = "AdminGroup"
  val GroupTypeInvestor = "InvestorGroup"

  val types = Seq(GroupTypeAdmin, GroupTypeInvestor)
}