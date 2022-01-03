package com.krykowski

import cats.effect.{ConcurrentEffect, ExitCode, IO, Timer}
import org.http4s.server.blaze.BlazeServerBuilder
import com.krykowski.api._
import com.krykowski.config.{AppConfig, Config, ForexSystemConfig, TokenConfig}
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

object HttpServer {
  private val logger = LoggerFactory.getLogger(getClass)

  def create(fs: ForexSystemConfig, token: TokenConfig, app: AppConfig)(implicit concurrentEffect: ConcurrentEffect[IO], timer: Timer[IO]): IO[ExitCode] = {
    logger.info("Starting HTTP server")

    logger.info(s"Provided forex config $fs")
    logger.info(s"Provided token config: $token")
    logger.info(s"Provided app config: $app")

    val externalApi = new CurrencyApi(fs, token)

    for {
      exitCode <- BlazeServerBuilder
        .apply[IO](ExecutionContext.global)
        .bindHttp(app.port, app.host)
        .withHttpApp(new CurrencyService(externalApi).routes.orNotFound).serve.compile.lastOrError
    } yield exitCode
  }
}