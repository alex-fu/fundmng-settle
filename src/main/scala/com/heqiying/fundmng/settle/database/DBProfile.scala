package com.heqiying.fundmng.settle.database

import com.heqiying.fundmng.settle.common.{ AppConfig, LazyLogging }
import slick.driver.JdbcProfile

import scala.concurrent.duration._

sealed trait DBProfile {
  val profile: JdbcProfile
  val timeoutDuration: FiniteDuration
  val db: slick.jdbc.JdbcBackend.DatabaseDef
  val jodaSupport: com.github.tototoshi.slick.GenericJodaSupport
}

trait PostgresDB extends DBProfile with LazyLogging {
  val jodaSupport = com.github.tototoshi.slick.PostgresJodaSupport
  val profile = slick.driver.PostgresDriver
  val timeoutDuration = 60.seconds
  val db = slick.jdbc.JdbcBackend.Database.forConfig(s"app.rds.postgresql")
}

trait MySQLDB extends DBProfile {
  val profile = slick.driver.MySQLDriver
  val timeoutDuration = 60.seconds
  val jodaSupport = com.github.tototoshi.slick.MySQLJodaSupport
  val db = slick.jdbc.JdbcBackend.Database.forConfig(s"app.rds.mysql")
}

object PostgresDB extends PostgresDB

object MySQLDB extends MySQLDB

object MainDBProfile {
  private val dbType = AppConfig.app.rds.`type`
  private val pf: DBProfile = dbType match {
    case "postgresql" => PostgresDB
    case "mysql" => MySQLDB
    case _ => MySQLDB
  }
  val profile = pf.profile
  val timeoutDuration = pf.timeoutDuration
  val db = pf.db
  val jodaSupport = pf.jodaSupport
}

