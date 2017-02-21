package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, FundNavTable }
import slick.lifted.TableQuery

class FundNavDAO extends CommonDAO[FundNavTable#TableElementType, FundNavTable] {
  override def tableQ: TableQuery[FundNavTable] = DBSchema.fundNavs

  override def pk: String = "id"
}
