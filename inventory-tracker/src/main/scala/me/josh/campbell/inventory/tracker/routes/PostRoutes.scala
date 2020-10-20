package me.josh.campbell.inventory.tracker

import cats.implicits._
import cats.effect.Sync
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.http4s.twirl._
import doobie._

object PostRoutes extends Routes {
  def publicRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] =
    HttpRoutes.empty

  def authedRoutes[F[_]: Sync: Transactor](implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._
    authedService((userId: UserId) =>
      HttpRoutes.of {
        case GET -> Root / "posts" =>
          for {
            posts <- Post.all(userId)
            response <- Ok(Post.index(posts))
          } yield response
        case GET -> Root / "item" / itemId / "post" / "add" =>
          Ok(Post.add(ItemId(itemId)))
        case GET -> Root / "post" / id =>
          for {
            post <- Post.find(PostId(id), userId)
            response <- Ok(post.show)
          } yield response

        case req @ POST -> Root / "item" / itemId / "post" / "create" =>
          for {
            form <- req.as[UrlForm]
            post <- Post.fromUrlForm(form, ItemId(itemId)).flatMap(_.save(userId))
            response <- Redirect(post.showUrl)
          } yield response
        case GET -> Root / "post" / id / "edit" =>
          for {
            post <- Post.find(PostId(id), userId)
            response <- Ok(post.edit)
          } yield response
        case req @ POST -> Root / "item" / itemId / "post" / id / "update" =>
          for {
            form <- req.as[UrlForm]
            post <- Post
              .fromUrlForm(form, ItemId(itemId))
              .map(_.copy(id = Some(PostId(id))))
              .flatMap(_.update(userId))
            response <- Redirect(post.showUrl)
          } yield response
        case GET -> Root / "post" / id / "destroy" =>
          for {
            _ <- Post.destroy(Some(PostId(id)), userId)
            response <- Redirect(Post.indexUrl)
          } yield response
      }
    )
  }
}
