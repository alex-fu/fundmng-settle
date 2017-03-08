package com.heqiying.fundmng.settle.domain

import java.sql.Date

sealed abstract class SettleParam

case class OpenDaySettleParam(fundUuid: String, openDate: Date) extends SettleParam

