package com.krykowski.api

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.slf4j.LoggerFactory

import java.net.ConnectException

object FirstCurrencyQueryParameter extends QueryParamDecoderMatcher[String]("firstCurrency")

object SecondCurrencyQueryParameter extends QueryParamDecoderMatcher[String]("secondCurrency")

class CurrencyService(forexApi: ForexApi) extends Http4sDsl[IO] {

  private val logger = LoggerFactory.getLogger(getClass)

  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "rates" :?
      FirstCurrencyQueryParameter(firstCurrency) +& SecondCurrencyQueryParameter(secondCurrency) =>
      logger.info("Communicating with external API")
      forexApi.getRates(firstCurrency, secondCurrency) match {
        case Right(request) => request.body match {
          case Left(_) => Ok("Request was executed successfully but response body was empty.")
          case Right(actualMessage) => Ok(s"Request executed successfully! :) $actualMessage")
        }
        case Left(exception) => exception match {
          case _: ConnectException =>
            logger
              .info(s"Error while talking to external API. " +
                s"Failed with Connection Error and message: ${exception.getMessage}")
            InternalServerError("Sorry, we couldn't connect to external forex system.")
          case _ =>
            logger
              .info(s"Error while talking to external API. " +
                s"Failed with ${exception.toString}")
            InternalServerError("There was some problem. Sorry!")
        }
      }
  }
}
