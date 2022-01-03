package com.krykowski.api

import cats.effect.IO
import com.krykowski.api.TestUtils._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{Request, Response, Status, Uri}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.net.ConnectException

class CurrencyServiceSpec extends AnyWordSpec with MockFactory with Matchers {

  private val forexApi = stub[ForexApi]
  private val service = new CurrencyService(forexApi).routes

  val correctUri = Uri.unsafeFromString(s"/rates?firstCurrency=$USD&secondCurrency=$JPY")

  "CurrencyService" should {
    "return message when forex API is not available" in {
      (forexApi.getRates _)
        .when(USD, JPY)
        .returns(Left(new ConnectException("No host available")))

      val response =
        serve(Request[IO](GET, correctUri))
      response.status shouldBe Status.InternalServerError
      response.as[String].unsafeRunSync() should contain
      """  Sorry, we couldn't connect to external forex system. """
    }

    "return message when forex API responded with an exception" in {
      (forexApi.getRates _)
        .when(USD, JPY)
        .returns(Left(new Exception("Something went wrong")))

      val response =
        serve(Request[IO](GET, correctUri))
      response.status shouldBe Status.InternalServerError
      response.as[String].unsafeRunSync() should contain
      """  There was some problem. Sorry! """
    }

    "return currency rate" in {
      (forexApi.getRates _)
        .when(USD, JPY)
        .returns(Right(com.softwaremill.sttp.Response.ok(exampleResponseWithRates)))

      val response =
        serve(Request[IO](GET, correctUri))
      response.status shouldBe Status.Ok
      response.bodyText.compile.toList.unsafeRunSync().head shouldEqual
        s"""Request executed successfully! :) $exampleResponseWithRates"""
    }

    "return message when forex API responded with empty body" in {
      (forexApi.getRates _)
        .when(USD, JPY)
        .returns(Right(com.softwaremill.sttp.Response(Left(""), 200, "status")))

      val response =
        serve(Request[IO](GET, correctUri))
      response.status shouldBe Status.Ok
      response.bodyText.compile.toList.unsafeRunSync().head shouldEqual
      """Request was executed successfully but response body was empty."""
    }

    "return message when not supported currency is used" in {
      (forexApi.getRates _)
        .when(notSupportedCurrency, JPY)
        .returns(Right(com.softwaremill.sttp.Response.ok(currencyNotSupportedResponse)))

      val response =
        serve(Request[IO](GET,
          Uri.unsafeFromString(s"/rates?firstCurrency=$notSupportedCurrency&secondCurrency=$JPY")))
      response.status shouldBe Status.Ok
      response.bodyText.compile.toList.unsafeRunSync().head shouldEqual
      s"""Request executed successfully! :) $currencyNotSupportedResponse"""
    }

  }

  private def serve(request: Request[IO]): Response[IO] = {
    service.orNotFound(request).unsafeRunSync()
  }

}
