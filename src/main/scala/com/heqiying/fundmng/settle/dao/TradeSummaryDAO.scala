package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, TradeSummaryTable, TradeTable }
import slick.lifted.TableQuery

class TradeSummaryDAO extends CommonDAO[TradeSummaryTable#TableElementType, TradeSummaryTable] {
  override def tableQ: TableQuery[TradeSummaryTable] = DBSchema.tradeSummaries

  override def pk: String = "id"
}
