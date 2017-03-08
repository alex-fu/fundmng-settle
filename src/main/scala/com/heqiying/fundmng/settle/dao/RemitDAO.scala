package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.database.MainDBProfile._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.model.{ DBSchema, Remit, RemitTable }
import slick.lifted.TableQuery

import scala.concurrent.Future

object RemitDAO extends CommonDAO[RemitTable#TableElementType, RemitTable] {
  override def tableQ: TableQuery[RemitTable] = DBSchema.remits

  override def pk: String = "id"

  def getBySettleUuid(settleUuid: String): Future[Seq[Remit]] = {
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

