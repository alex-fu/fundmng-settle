package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, PurchaseTable }
import slick.lifted.TableQuery

class PurchaseDAO extends CommonDAO[PurchaseTable#TableElementType, PurchaseTable] {
  override def tableQ: TableQuery[PurchaseTable] = DBSchema.purchases

  override def pk: String = "id"
}
