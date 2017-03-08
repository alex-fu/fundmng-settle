package com.heqiying.fundmng.settle.dao

import java.sql.Date

import com.heqiying.fundmng.settle.model._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.database.MainDBProfile._
import slick.lifted.TableQuery

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FundDAO extends CommonDAO[FundTable#TableElementType, FundTable] {
  override def tableQ: TableQuery[FundTable] = DBSchema.funds

  override def pk: String = "id"

  def isFundExist(fundUuid: String): Future[Boolean] = {
    val q0 = tableQ.filter(_.uuid === fundUuid).exists.result
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }

  def isOpenDateExist(fundUuid: String, openDate: Date): Future[Boolean] = {
    // FIXME
    Future(true)
  }

  def getReviewed(fundUuid: String): Future[Option[Fund]] = {
    val ids = tableQ.filter(_.reviewState === Funds.ReviewedReviewState).groupBy(f => f.uuid).map {
      case (_, funds) => funds.map(_.id).max
    }
    val q0 = tableQ.filter(_.id in ids).filter(_.isDel === Funds.Alive).result.headOption
    sqlDebug(q0.statements.mkString(";\n"))
    db.run(q0)
  }
}
