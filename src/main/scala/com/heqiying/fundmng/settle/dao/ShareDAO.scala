package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, Share, ShareTable }
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.database.MainDBProfile._
import slick.lifted.TableQuery

import scala.concurrent.Future

object ShareDAO extends CommonDAO[ShareTable#TableElementType, ShareTable] {
  override def tableQ: TableQuery[ShareTable] = DBSchema.shares

  override def pk: String = "id"

  def getBySettleUuid(settleUuid: String) = {
    val q0 = tableQ.filter(_.settleUuid === settleUuid).result
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }
  def deleteBySettleUuid(settleUuid: String) = {
    val q0 = tableQ.filter(_.settleUuid === settleUuid).delete
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }
}

