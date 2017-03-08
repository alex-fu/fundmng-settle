package com.heqiying.fundmng.settle.domain

import java.util.UUID

import com.heqiying.fundmng.settle.dao._
import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import com.heqiying.fundmng.settle.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SettleDomainService {
  self: ParamChecker with TradeAggregator with SettleCalculator with SettleRepository =>

  /**
   * COMMON SETTLE WORKFLOW:
   *
   *                        START
   *                          |
   *                     checkParams
   *                          |
   *                   loadCurrShares
   *                          |
   *                   aggregateTrades
   *                          |
   *     +--------------------+-------------------+
   *     |                    |                   |
   * calcShares      calcTradeSummaries  calcInvestStatement
   *     |                    |                   |
   *     +--------------------+-------------------+
   *                          |
   *                      newSettle
   *                          |
   *                         END
   */

  def getLatestSettle(fundUuid: String): Future[Option[Settle]] = {
    SettleDAO.getLatest(fundUuid)
  }

  def settle(param: SettleParam): Future[String] = {
    val prepareStage = for {
      nav <- self.checkParams(param)
      settleUuid = UUID.randomUUID().toString
      currShares <- self.loadCurrentShares(param)
      settle <- self.genNewSettle(settleUuid, param)
      (newShares, trades) <- self.aggregateTrades(param, settleUuid, currShares, nav)
    } yield (settleUuid, currShares, settle, newShares, trades)

    val settleStage = for {
      (settleUuid, currShares, settle, newShares, trades) <- prepareStage
    } yield {
      val tradeSummaries = calcTradeSummaries(settleUuid, trades)
      val statements = calcInvestStatements(settleUuid, newShares, trades)
      val remits = calcRemits(settleUuid, trades)
      newSettle(settleUuid, settle, newShares, trades, tradeSummaries, statements, remits)
    }

    settleStage.flatMap(identity)
  }

  def dropSettle(settle: Settle): Future[String] = {
    for {
      settleUuid <- SettleDAO.update(settle.uuid, settle.copy(state = Settles.DroppedState)).map(_ => settle.uuid)
    } yield {
      for {
        _ <- dropSettleResults(settleUuid)
      } yield ()
      settleUuid
    }
  }

  def confirmSettle(settle: Settle): Future[String] = {
    SettleDAO.update(settle.uuid, settle.copy(state = Settles.ConfirmedState)).map(_ => settle.uuid)
  }

}