package com.heqiying.fundmng.settle.dao

import java.sql.Date

import com.heqiying.fundmng.settle.model.{ DBSchema, Settle, SettleTable, Settles }
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.database.MainDBProfile._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SettleDAO extends CommonDAO[SettleTable#TableElementType, SettleTable] {
  override def tableQ: TableQuery[SettleTable] = DBSchema.settles

  override def pk: String = "uuid"

  def getLatest(fundUuid: String): Future[Option[Settle]] = {
    val q0 = tableQ.filter(_.fundUuid === fundUuid).filter(_.state =!= Settles.DroppedState).sortBy(_.id.desc).take(1).result
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0).map(_.headOption)
  }

  def getAllByFund(fundUuid: String) = {
    val q0 = tableQ.filter(_.fundUuid === fundUuid).filter(_.state =!= Settles.DroppedState).sortBy(_.id.desc).result
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }

  def isOpenDateSettled(fundUuid: String, openDate: Date) = {
    val q0 = tableQ.filter(_.fundUuid === fundUuid).filter(_.openDate === openDate).
      filter(x => x.state === Settles.SettledState || x.state === Settles.ConfirmedState).exists.result
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }

  def get(fundUuid: String, openDate: Date) = {
    val q0 = tableQ.filter(_.fundUuid === fundUuid).filter(_.openDate === openDate).
      filter(_.state =!= Settles.DroppedState).result.headOption
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }
}
