package me.josh.campbell.inventory.tracker

import cats.implicits._
import cats.effect.Sync
import doobie._
import doobie.implicits._
import org.http4s._
import java.util.UUID

final case class PostId(value: UUID)
object PostId {
  def apply(id: String): PostId = PostId(UUID.fromString(id))
  def random: PostId = PostId(UUID.randomUUID)
}

final case class Post(
    outlet: String,
    link: String,
    item_id: ItemId,
    createdAt: Option[Date],
    updatedAt: Option[Date],
    id: Option[PostId],
    userId: Option[UserId]
) {
  def save[F[_]: Sync: Transactor](userId: UserId): F[Post] = Post.create[F](this, userId)

  def update[F[_]: Sync: Transactor](userId: UserId): F[Post] = Post.update[F](this, userId)

  def destroy[F[_]: Sync: Transactor](userId: UserId): F[Int] = Post.destroy[F](this.id, userId)
}

object Post extends Model with PostQueries

trait PostQueries extends Queries {
  def all[F[_]: Sync](userId: UserId)(implicit XA: Transactor[F]): F[List[Post]] =
    sql"""
      select * from inventory_tracker_post where user_id = ${userId}
    """.query[Post].to[List].transact(XA)

  def allByItem[F[_]: Sync](itemId: ItemId, userId: UserId)(implicit XA: Transactor[F]): F[List[Post]] =
    sql"""
      select * from inventory_tracker_post where item_id = ${itemId} and user_id = ${userId}
    """.query[Post].to[List].transact(XA)

  def find[F[_]: Sync](postId: PostId, userId: UserId)(implicit XA: Transactor[F]): F[Post] =
    sql"""
     select * from inventory_tracker_post where id = ${postId.value} and user_id = ${userId.id}
    """.query[Post].unique.transact(XA)

  def create[F[_]: Sync](post: Post, userId: UserId)(implicit XA: Transactor[F]): F[Post] =
    sql"""
      insert into inventory_tracker_post (outlet, link, item_id, created_at, id, user_id)
      values
      (
        ${post.outlet},
        ${post.link},
        ${post.item_id},
        ${Date.now},
        ${PostId.random},
        ${userId.id}
      );
    """.update
      .withUniqueGeneratedKeys[Post]("outlet", "link", "item_id", "created_at", "updated_at", "id", "user_id")
      .transact(XA)

  def update[F[_]: Sync](post: Post, userId: UserId)(implicit XA: Transactor[F]): F[Post] =
    sql"""
      update inventory_tracker_post set
        outlet = ${post.outlet},
        link = ${post.link},
        item_id = ${post.item_id},
        updated_at = ${Date.now}
      where id = ${post.id.map(_.value)}
      and user_id = ${userId.id}
    """.update
      .withUniqueGeneratedKeys[Post]("outlet", "link", "item_id", "created_at", "updated_at", "id", "user_id")
      .transact(XA)

  def destroy[F[_]: Sync](id: Option[PostId], userId: UserId)(implicit XA: Transactor[F]): F[Int] =
    sql"""delete from inventory_tracker_post where id = ${id} and user_id = ${userId.id}""".update.run.transact(XA)
}
