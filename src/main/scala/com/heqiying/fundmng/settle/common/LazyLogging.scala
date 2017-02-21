package com.heqiying.fundmng.settle.common

import com.typesafe.scalalogging.{ LazyLogging => LL }

trait LazyLogging extends LL {
  lazy val isSqlDebugEnabled = AppConfig.config.getBoolean("fundmng-gate.logs.sql.debug")

  def sqlDebug(s: String) = {
    if (isSqlDebugEnabled) {
      logger.debug(s)
    } else {}
  }
}
