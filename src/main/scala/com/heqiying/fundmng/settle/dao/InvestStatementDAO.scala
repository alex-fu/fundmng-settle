package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, InvestStatement, InvestStatementTable }
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.database.MainDBProfile._
import slick.lifted.TableQuery

import scala.concurrent.Future

object InvestStatementDAO extends CommonDAO[InvestStatementTable#TableElementType, InvestStatementTable] {
  override def tableQ: TableQuery[InvestStatementTable] = DBSchema.investStatements

  override def pk: String = "id"

  def getBySettleUuid(settleUuid: String): Future[Seq[InvestStatement]] = {
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

