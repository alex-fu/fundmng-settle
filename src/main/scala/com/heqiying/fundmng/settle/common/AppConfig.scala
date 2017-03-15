package com.heqiying.fundmng.settle.common

import com.heqiying.fundmng.settle.common.AppConfig._
import com.typesafe.config.ConfigFactory

case class AppConfig(admin: AdminConfig, api: ApiConfig, activiti: ActivitiConfig, rds: RDSConfig)

object AppConfig {
  import com.typesafe.config.Config
  import com.heqiying.konfig.Konfig._

  case class AdminConfig(name: String, port: Int)
  case class ApiConfig(authorization: Boolean, `private`: PrivateConfig)
  case class PrivateConfig(allowAllAddress: Boolean, allowedAddresses: List[String])
  case class ActivitiConfig(host: String, port: Int, baseUri: String, defaultPassword: String, dummyUser: String, dummyPassword: String)
  case class RDSConfig(`type`: String)

  def fromConfig(config: Config): AppConfig = config.read[AppConfig]("app")

  lazy val config = ConfigFactory.load()
  lazy val app: AppConfig = fromConfig(config)
}
