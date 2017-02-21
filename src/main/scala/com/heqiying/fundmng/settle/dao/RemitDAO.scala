package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, RemitTable }
import slick.lifted.TableQuery

class RemitDAO extends CommonDAO[RemitTable#TableElementType, RemitTable] {
  override def tableQ: TableQuery[RemitTable] = DBSchema.remits

  override def pk: String = "id"
}

