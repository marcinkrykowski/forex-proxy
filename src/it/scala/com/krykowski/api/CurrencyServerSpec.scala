package com.krykowski.api

import cats.effect.{IO, Timer}
import com.krykowski.HttpServer
import org.http4s.{Method, Request, Status, Uri}
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import com.krykowski.api.TestUtils._

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class CurrencyServerSpec
  extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with Eventually {

  private implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  implicit private val cs = IO.contextShift(global)
  implicit private val cf = IO.ioConcurrentEffect

  private lazy val client = BlazeClientBuilder[IO](global).resource

  private lazy val fs = ItConfig.forexSystem
  private lazy val token = ItConfig.token
  private lazy val app = ItConfig.appConfig

  private lazy val urlStart =
    s"http://${app.host}:${app.port}"

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(
    timeout = scaled(Span(5, Seconds)),
    interval = scaled(Span(100, Millis))
  )

  override def beforeAll(): Unit = {
    HttpServer.create(fs, token, app).unsafeRunAsyncAndForget()
    eventually {
      client
        .use(_.statusFromUri(Uri.unsafeFromString(s"$urlStart/rates?firstCurrency=$USD&secondCurrency=$JPY")))
        .unsafeRunSync() shouldBe Status.Ok
    }
    ()
  }

  "Currency server" should {
    "return a currency rate" in {
      val request = Request[IO](
        method = Method.GET,
        uri = Uri.unsafeFromString(s"$urlStart/rates?firstCurrency=$USD&secondCurrency=$JPY")
      )
      val response = client.use(_.expect[String](request)).unsafeRunSync()
      response should include ("Request executed successfully! :)")
    }
    "return a message when currency is not supported" in {
      val request = Request[IO](
        method = Method.GET,
        uri = Uri.unsafeFromString(s"$urlStart/rates?firstCurrency=$notSupportedCurrency&secondCurrency=$JPY")
      )
      val response = client.use(_.expect[String](request)).unsafeRunSync()
      response should include ("Invalid Currency Pair")
    }
  }

}
