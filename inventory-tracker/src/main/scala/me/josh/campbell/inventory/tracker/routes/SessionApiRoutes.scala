package me.josh.campbell.inventory.tracker

import cats.implicits._
import cats.effect.Sync
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._
import org.http4s._
import doobie._

object SessionApiRoutes extends Routes {
  def publicRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._
    HttpRoutes.of {
      case params @ POST -> Root / "api" / "v1" / "login" => {
        for {
          session <- params.as[Session]
          user <- session.findUser
          response <- user.fold(BadRequest(""))(u => Ok(Session.cookie(u).map(_.content)))
        } yield response
      }
    }
  }

  def authedRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._
    authedService((_: UserId) => HttpRoutes.empty)
  }
}
