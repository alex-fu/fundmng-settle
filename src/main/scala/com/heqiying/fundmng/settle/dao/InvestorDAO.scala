package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, InvestorTable }
import slick.lifted.TableQuery

object InvestorDAO extends CommonDAO[InvestorTable#TableElementType, InvestorTable] {
  override def tableQ: TableQuery[InvestorTable] = DBSchema.investors

  override def pk: String = "id"
}
