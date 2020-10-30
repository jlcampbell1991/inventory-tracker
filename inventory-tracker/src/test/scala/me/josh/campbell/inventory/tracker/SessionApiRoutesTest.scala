package me.josh.campbell.inventory.tracker

import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import org.http4s.twirl._

final class SessionApiRoutesTest extends BaseTest {
  import DBDriver._
  val user: User = User(java.util.UUID.randomUUID.toString, Password("pass123"), None).save[IO].unsafeRunSync
  val session: Session = Session(user.name, user.password)
  val cookie: Option[String] = Session.cookie(user).map(_.content)

  """POST -> Root / "api" / "v1" / "login"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.POST, uri = Uri.uri("/api/v1/login")).withEntity(
            session
          )
        ),
      Status.Ok,
      cookie
    )
  }
}
