package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, ShareTable }
import slick.lifted.TableQuery

class ShareDAO extends CommonDAO[ShareTable#TableElementType, ShareTable] {
  override def tableQ: TableQuery[ShareTable] = DBSchema.shares

  override def pk: String = "id"
}

