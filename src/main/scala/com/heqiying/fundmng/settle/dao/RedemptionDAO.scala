package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, RedemptionTable }
import slick.lifted.TableQuery

class RedemptionDAO extends CommonDAO[RedemptionTable#TableElementType, RedemptionTable] {
  override def tableQ: TableQuery[RedemptionTable] = DBSchema.redemptions

  override def pk: String = "id"
}
