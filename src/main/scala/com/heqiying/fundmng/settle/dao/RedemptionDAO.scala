package com.heqiying.fundmng.settle.dao

import java.sql.Date

import com.heqiying.fundmng.settle.database.MainDBProfile._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.model.{ DBSchema, Redemption, RedemptionTable }
import slick.lifted.TableQuery

import scala.concurrent.Future

object RedemptionDAO extends CommonDAO[RedemptionTable#TableElementType, RedemptionTable] {
  override def tableQ: TableQuery[RedemptionTable] = DBSchema.redemptions

  override def pk: String = "id"

  def get(fundUuid: String, openDate: Date): Future[Seq[Redemption]] = {
    val q0 = tableQ.filter(x => x.fundUuid === fundUuid && x.openDate === openDate).result
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }
}
