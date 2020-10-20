package me.josh.campbell.inventory.tracker

import cats.implicits._
import cats.effect.Sync
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.http4s.twirl._
import doobie._

object ItemRoutes extends Routes {
  def publicRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] =
    HttpRoutes.empty

  def authedRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._
    authedService((userId: UserId) =>
      HttpRoutes.of {
        case GET -> Root =>
          for {
            items <- Item.all(userId)
            response <- Ok(Item.index(items))
          } yield response
        case GET -> Root / "item" / "add" => Ok(Item.add)
        case GET -> Root / "item" / id =>
          for {
            item <- Item.find(ItemId(id), userId)
            response <- Ok(item.show)
          } yield response
        case req @ POST -> Root / "item" / "create" =>
          for {
            form <- req.as[UrlForm]
            item <- Item.fromUrlForm(form).flatMap(_.save(userId))
            response <- Redirect(item.showUrl)
          } yield response
        case GET -> Root / "item" / id / "edit" =>
          for {
            item <- Item.find(ItemId(id), userId)
            posts <- Post.allByItem(ItemId(id), userId)
            response <- Ok(item.edit(posts))
          } yield response
        case req @ POST -> Root / "item" / id / "update" =>
          for {
            form <- req.as[UrlForm]
            item <- Item
              .fromUrlForm(form)
              .map(_.copy(id = Some(ItemId(id))))
              .flatMap(_.update(userId))
            response <- Redirect(item.showUrl)
          } yield response
        case GET -> Root / "item" / id / "destroy" =>
          for {
            _ <- Item.destroy(Some(ItemId(id)), userId)
            response <- Redirect(Item.indexUrl)
          } yield response
      }
    )
  }
}
