package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, InvestStatementTable }
import slick.lifted.TableQuery

class InvestStatementDAO extends CommonDAO[InvestStatementTable#TableElementType, InvestStatementTable] {
  override def tableQ: TableQuery[InvestStatementTable] = DBSchema.investStatements

  override def pk: String = "id"
}

