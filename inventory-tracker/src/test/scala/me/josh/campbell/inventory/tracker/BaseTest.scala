package me.josh.campbell.inventory.tracker

import cats.effect._
import cats.implicits._
import org.http4s._
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

abstract class BaseTest extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with TypeCheckedTripleEquals {
  import DBDriver._
  DB.initialize(DBDriver.XA).sequence.unsafeRunSync

  val service: HttpRoutes[IO] = Routes.routes(HttpRoutes.empty)

  def check[A](actual: IO[Response[IO]], expectedStatus: org.http4s.Status, expectedBody: Option[A])(
      implicit ev: EntityDecoder[IO, A]
  ): Assertion = {
    val actualResp = actual.unsafeRunSync
    val bodyCheck = expectedBody match {
      case Some(_) =>
        expectedBody.fold[Boolean](actualResp.body.compile.toVector.unsafeRunSync.isEmpty)(expected =>
          actualResp.as[A].unsafeRunSync == expected
        )
      case None => true
    }
    assert(actualResp.status == expectedStatus && bodyCheck)
  }
}
