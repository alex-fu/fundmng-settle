package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, DividendTable, DividendTypeChangeTable }
import slick.lifted.TableQuery

class DividendDAO extends CommonDAO[DividendTable#TableElementType, DividendTable] {
  override def tableQ: TableQuery[DividendTable] = DBSchema.dividends

  override def pk: String = "id"
}
