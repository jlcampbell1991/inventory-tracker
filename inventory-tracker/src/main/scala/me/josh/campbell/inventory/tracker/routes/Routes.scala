package me.josh.campbell.inventory.tracker

import cats.data._
import cats.effect.Sync
import cats.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers._
import org.http4s.HttpRoutes
import org.http4s.Uri._
import doobie._

trait Routes {
  def Redirect[F[_]: Sync](uri: String)(implicit dsl: Http4sDsl[F]) = {
    import dsl._
    SeeOther(Location(Uri(authority = Some(Authority(host = RegName(uri))))))
  }

  def authedService[F[_]: Sync: Http4sDsl](service: UserId => HttpRoutes[F]): HttpRoutes[F] = Kleisli {
    (req: Request[F]) =>
      Session.isLoggedIn(req.headers) match {
        case Some(id: UserId) => service(id)(req)
        case None => OptionT.liftF(Redirect(Session.loginUrl))
      }
  }
}

object Routes {
  private def PublicRoutes[F[_]: Sync: Transactor: Http4sDsl]: HttpRoutes[F] =
    UserRoutes.publicRoutes[F] <+>
      SessionRoutes.publicRoutes[F]

  private def AuthedRoutes[F[_]: Sync: Transactor: Http4sDsl]: HttpRoutes[F] =
    UserRoutes.authedRoutes[F] <+>
      SessionRoutes.authedRoutes[F] <+> ItemRoutes.authedRoutes[F] <+> PostRoutes.authedRoutes[F]

  def routes[F[_]: Sync: Transactor](AssetsRoutes: HttpRoutes[F]): HttpRoutes[F] = {
    implicit val dsl = new Http4sDsl[F] {}

    AssetsRoutes <+>
      PublicRoutes[F] <+>
      AuthedRoutes[F]
  }
}
