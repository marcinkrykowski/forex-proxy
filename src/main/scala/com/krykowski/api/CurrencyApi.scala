package com.krykowski.api

import com.krykowski.config.{ForexSystemConfig, TokenConfig}
import com.softwaremill.sttp._
import org.slf4j.LoggerFactory


trait ForexApi {
  def getRates(firstCurrency: String, secondCurrency: String): Either[Throwable, Response[String]]
}

class CurrencyApi(forex: ForexSystemConfig, token: TokenConfig) extends ForexApi {

  private val logger = LoggerFactory.getLogger(getClass)

  def getRates(firstCurrency: String, secondCurrency: String): Either[Throwable, Response[String]] = {
    val currencies = firstCurrency + secondCurrency
    logger.info(s"Request execution started for currencies: $firstCurrency and $secondCurrency")
    val request = sttp
      .header(token.name, token.value)
      .get(uri"${forex.host}:${forex.port}/${forex.endpoint}?pair=$currencies")
    implicit val backend = TryHttpURLConnectionBackend()
    logger.info(s"Request being executed: ${request.toCurl}")
    request.send().toEither
  }
}
