package me.josh.campbell.inventory.tracker

import cats.implicits._
import cats.effect.Sync
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._
import org.http4s._
import doobie._

object UserApiRoutes extends Routes {
  def publicRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._
    HttpRoutes.of {
      case params @ POST -> Root / "api" / "v1" / "signup" =>
        for {
          user <- params.as[User].flatMap(_.save)
          response <- Ok(Session.cookie(user).map(_.content))
        } yield response
    }
  }
}
