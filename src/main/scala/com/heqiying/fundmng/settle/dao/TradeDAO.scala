package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, TradeTable }
import slick.lifted.TableQuery

class TradeDAO extends CommonDAO[TradeTable#TableElementType, TradeTable] {
  override def tableQ: TableQuery[TradeTable] = DBSchema.trades

  override def pk: String = "id"
}

