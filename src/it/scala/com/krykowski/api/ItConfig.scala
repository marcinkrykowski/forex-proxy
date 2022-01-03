package com.krykowski.api

import com.krykowski.config.{AppConfig, ForexSystemConfig, TokenConfig}
import com.typesafe.config.ConfigFactory

object ItConfig {
  private val config = ConfigFactory.load("it.conf")

  val forexSystem = ForexSystemConfig(
    config.getString("forex.host"),
    config.getString("forex.endpoint"),
    config.getString("forex.port")
  )

  val token = TokenConfig(
    config.getString("token.value"),
    config.getString("token.name")
  )

  val appConfig = AppConfig (
    config.getString("application.host"),
    config.getInt("application.port")
  )
}
