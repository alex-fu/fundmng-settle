package com.heqiying.fundmng.settle.domain

import com.heqiying.fundmng.settle.dao.{ FundDAO, FundNavDAO, SettleDAO }
import com.heqiying.fundmng.settle.utils.AppErrors

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ParamChecker {
  def checkParams(param: SettleParam): Future[BigDecimal]
}

trait OpenDayParamChecker extends ParamChecker {
  def checkParams(param: SettleParam): Future[BigDecimal] = {
    param match {
      case OpenDaySettleParam(fundUuid, openDate) =>
        val f: Future[(Boolean, Boolean, Boolean, Option[BigDecimal])] = for {
          r1 <- FundDAO.isFundExist(fundUuid)
          r2 <- FundDAO.isOpenDateExist(fundUuid, openDate)
          r3 <- SettleDAO.isOpenDateSettled(fundUuid, openDate)
          r4 <- FundNavDAO.getNav(fundUuid, openDate)
        } yield (r1, r2, r3, r4)
        f.map {
          case (false, _, _, _) => throw AppErrors.InvalidFund()
          case (_, false, _, _) => throw AppErrors.InvalidOpenDay()
          case (_, _, true, _) => throw AppErrors.AlreadySettled()
          case (_, _, _, None) => throw AppErrors.NavNotExist()
          case (_, _, _, Some(r)) => r
        }

      case _ => Future.failed(AppErrors.InvalidSettleParam())
    }
  }
}
