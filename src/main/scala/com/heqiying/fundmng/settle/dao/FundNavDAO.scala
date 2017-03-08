package com.heqiying.fundmng.settle.dao

import java.sql.Date

import com.heqiying.fundmng.settle.model.{ DBSchema, FundNavTable }
import slick.lifted.TableQuery
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.database.MainDBProfile._

import scala.concurrent.Future

object FundNavDAO extends CommonDAO[FundNavTable#TableElementType, FundNavTable] {
  override def tableQ: TableQuery[FundNavTable] = DBSchema.fundNavs

  override def pk: String = "id"

  def getNav(fundUuid: String, openDate: Date): Future[Option[BigDecimal]] = {
    val q0 = tableQ.filter(x => x.fundUuid === fundUuid && x.date === openDate).map(_.nav).result.headOption
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }

}
