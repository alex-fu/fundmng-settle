package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model._
import slick.lifted.TableQuery

class FundDAO extends CommonDAO[FundTable#TableElementType, FundTable] {
  override def tableQ: TableQuery[FundTable] = DBSchema.funds

  override def pk: String = "id"
}
