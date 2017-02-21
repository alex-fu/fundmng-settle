package com.heqiying.fundmng.settle.service

import com.heqiying.fundmng.settle.common.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait OpenDaySettleService extends LazyLogging {
  def opendaySettle(fundUuid: String, openDate: String): Future[Either[String, String]] = {
    Future(Left("not implemented"))
  }
}
