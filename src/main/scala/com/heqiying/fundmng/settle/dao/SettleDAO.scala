package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, SettleTable }
import slick.lifted.TableQuery

class SettleDAO extends CommonDAO[SettleTable#TableElementType, SettleTable] {
  override def tableQ: TableQuery[SettleTable] = DBSchema.settles

  override def pk: String = "uuid"
}
