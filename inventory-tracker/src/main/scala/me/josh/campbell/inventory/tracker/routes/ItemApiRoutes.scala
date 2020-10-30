package me.josh.campbell.inventory.tracker

import cats.implicits._
import cats.effect.Sync
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.http4s.twirl._
import doobie._

object ItemApiRoutes extends Routes {
  def publicRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._
    HttpRoutes.empty
  }

  def authedRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._
    val userId: UserId = UserId("e5554a35-1bac-49bc-b63f-834321a4fe47")
    // authedService((userId: UserId) =>
    HttpRoutes.of {
      case GET -> Root / "api" / "v1" =>
        for {
          items <- Item.all(userId)
          response <- Ok(items)
        } yield response
      case GET -> Root / "api" / "v1" / "item" / id =>
        for {
          item <- Item.find(ItemId(id), userId)
          response <- Ok(item)
        } yield response
      case req @ POST -> Root / "api" / "v1" / "item" / "create" =>
        for {
          form <- req.as[UrlForm]
          item <- Item.fromUrlForm(form).flatMap(_.save(userId))
          response <- Ok(item)
        } yield response
      case req @ POST -> Root / "api" / "v1" / "item" / id / "update" =>
        for {
          form <- req.as[UrlForm]
          item <- Item
            .fromUrlForm(form)
            .map(_.copy(id = Some(ItemId(id))))
            .flatMap(_.update(userId))
          response <- Ok(item)
        } yield response
      case GET -> Root / "api" / "v1" / "item" / id / "destroy" =>
        for {
          _ <- Item.destroy(Some(ItemId(id)), userId)
          response <- Redirect(Item.indexUrl)
        } yield response
    }
    // )
  }
}
