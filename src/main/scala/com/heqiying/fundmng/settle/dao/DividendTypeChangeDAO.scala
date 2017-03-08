package com.heqiying.fundmng.settle.dao

import java.sql.Date

import com.heqiying.fundmng.settle.database.MainDBProfile._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.model.{ DBSchema, DividendTypeChange, DividendTypeChangeTable }
import slick.lifted.TableQuery

import scala.concurrent.Future

object DividendTypeChangeDAO extends CommonDAO[DividendTypeChangeTable#TableElementType, DividendTypeChangeTable] {
  override def tableQ: TableQuery[DividendTypeChangeTable] = DBSchema.dividendTypeChanges

  override def pk: String = "id"

  def get(fundUuid: String, openDate: Date): Future[Seq[DividendTypeChange]] = {
    val q0 = tableQ.filter(x => x.fundUuid === fundUuid && x.openDate === openDate).result
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }
}
