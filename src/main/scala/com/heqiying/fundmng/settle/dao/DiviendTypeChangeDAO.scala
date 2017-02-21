package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, DividendTypeChangeTable }
import slick.lifted.TableQuery

class DiviendTypeChangeDAO extends CommonDAO[DividendTypeChangeTable#TableElementType, DividendTypeChangeTable] {
  override def tableQ: TableQuery[DividendTypeChangeTable] = DBSchema.dividendTypeChanges

  override def pk: String = "id"
}
