package me.josh.campbell.inventory.tracker

import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import org.http4s.twirl._

final class UserApiRoutesTest extends BaseTest {
  import DBDriver._
  val user: User = User(java.util.UUID.randomUUID.toString, Password("pass123"), None)
  val cookie: Option[String] = Session.cookie(user).map(_.content)

  """POST -> Root / "api" / "v1" / "signup"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.POST, uri = Uri.uri("/api/v1/signup")).withEntity(
            user
          )
        ),
      Status.Ok,
      cookie
    )
  }
}
