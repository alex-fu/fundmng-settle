package com.heqiying.fundmng.settle.dao

import com.heqiying.fundmng.settle.model.{ DBSchema, Trade, TradeTable }
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.database.MainDBProfile._
import slick.lifted.TableQuery

import scala.concurrent.Future

object TradeDAO extends CommonDAO[TradeTable#TableElementType, TradeTable] {
  override def tableQ: TableQuery[TradeTable] = DBSchema.trades

  override def pk: String = "id"

  def getBySettleUuid(settleUuid: String): Future[Seq[Trade]] = {
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

