package com.krykowski.config

import com.typesafe.config.ConfigFactory

object Config {

  private val config = ConfigFactory.load()

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
