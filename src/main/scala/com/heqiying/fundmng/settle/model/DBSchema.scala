package com.heqiying.fundmng.settle.model

object DBSchema {
  import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._

  val funds = TableQuery[FundTable]
  val fundNavs = TableQuery[FundNavTable]
  val investors = TableQuery[InvestorTable]
  val purchases = TableQuery[PurchaseTable]
  val redemptions = TableQuery[RedemptionTable]
  val dividendTypeChanges = TableQuery[DividendTypeChangeTable]
  val dividends = TableQuery[DividendTable]
  val settles = TableQuery[SettleTable]
  val trades = TableQuery[TradeTable]
  val tradeSummaries = TableQuery[TradeSummaryTable]
  val shares = TableQuery[ShareTable]
  val investStatements = TableQuery[InvestStatementTable]
  val remits = TableQuery[RemitTable]
}
