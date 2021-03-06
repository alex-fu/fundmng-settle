package com.heqiying.fundmng.settle.dao

import java.sql.Date

import com.heqiying.fundmng.settle.model.{ DBSchema, Purchase, PurchaseTable }
import com.heqiying.fundmng.settle.database.MainDBProfile._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

object PurchaseDAO extends CommonDAO[PurchaseTable#TableElementType, PurchaseTable] {
  override def tableQ: TableQuery[PurchaseTable] = DBSchema.purchases

  override def pk: String = "id"

  def get(fundUuid: String, openDate: Date): Future[Seq[Purchase]] = {
    val q0 = tableQ.filter(x => x.fundUuid === fundUuid && x.openDate === openDate).result
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }
}
