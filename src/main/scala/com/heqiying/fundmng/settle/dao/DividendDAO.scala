package com.heqiying.fundmng.settle.dao

import java.sql.Date

import com.heqiying.fundmng.settle.database.MainDBProfile._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.model._
import slick.lifted.TableQuery

import scala.concurrent.Future

object DividendDAO extends CommonDAO[DividendTable#TableElementType, DividendTable] {
  override def tableQ: TableQuery[DividendTable] = DBSchema.dividends

  override def pk: String = "id"

  def get(fundUuid: String, openDate: Date): Future[Option[Dividend]] = {
    val q0 = tableQ.filter(x => x.fundUuid === fundUuid && x.openDate === openDate).result.headOption
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }
}
