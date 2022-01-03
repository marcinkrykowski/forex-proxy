package com.krykowski.api

object TestUtils {
  val USD = "USD"
  val JPY = "JPY"
  val notSupportedCurrency = "CURRENCY"

  val currencyNotSupportedResponse = s"$notSupportedCurrency is not supported"
  val exampleResponseWithRates = """[{"from":"USD","to":"JPY","bid":0.61,"ask":0.82,"price":0.71,"time_stamp":"2019-01-01T00:00:00.000"}]"""
}
