package me.josh.campbell.inventory.tracker

import Date._
// import cats.implicits._
import cats.effect.Sync
import doobie._
import doobie.implicits._
import io.circe._, io.circe.generic.semiauto._
// import org.http4s._
import java.util.UUID

final case class ItemId(value: UUID)
object ItemId {
  def apply(id: String): ItemId = ItemId(UUID.fromString(id))
  def random: ItemId = ItemId(UUID.randomUUID)

  implicit val decoder: Decoder[ItemId] = io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
  implicit val encoder: Encoder[ItemId] = io.circe.generic.extras.semiauto.deriveUnwrappedEncoder
}

final case class Item(
    name: String,
    description: String,
    date_purchased: Date,
    date_sold: Option[Date],
    purchase_price: Double,
    sale_price: Option[Double],
    category: String,
    where_sold: Option[String],
    storage_location: String,
    photos_taken: Boolean,
    // createdAt: Option[Date],
    // updatedAt: Option[Date],
    id: Option[ItemId]
    // userId: Option[UserId]
) {
  def save[F[_]: Sync: Transactor](userId: UserId): F[Item] = Item.create[F](this, userId)

  def update[F[_]: Sync: Transactor](userId: UserId): F[Item] = Item.update[F](this, userId)

  def destroy[F[_]: Sync: Transactor](userId: UserId): F[Int] = Item.destroy[F](this.id, userId)

  private def formatCurrency(double: Double): String =
    "$" + f"$double%1.2f"

  def formattedPurchasePrice: String =
    formatCurrency(purchase_price)

  def formattedSalePrice: Option[String] =
    sale_price.map(formatCurrency(_))
}

object Item extends Model with ItemQueries with ItemCodec

trait ItemQueries extends Queries {
  def all[F[_]: Sync](userId: UserId)(implicit XA: Transactor[F]): F[List[Item]] =
    sql"""
      select
        name,
        description,
        date_purchased,
        date_sold,
        purchase_price,
        sale_price,
        category,
        where_sold,
        storage_location,
        photos_taken,
        id from inventory_tracker_item where user_id = ${userId}
    """.query[Item].to[List].transact(XA)

  def find[F[_]: Sync](itemId: ItemId, userId: UserId)(implicit XA: Transactor[F]): F[Item] =
    sql"""
     select
       name,
       description,
       date_purchased,
       date_sold,
       purchase_price,
       sale_price,
       category,
       where_sold,
       storage_location,
       photos_taken,
       id from inventory_tracker_item where id = ${itemId.value} and user_id = ${userId.id}
    """.query[Item].unique.transact(XA)

  def create[F[_]: Sync](item: Item, userId: UserId)(implicit XA: Transactor[F]): F[Item] =
    sql"""
      insert into inventory_tracker_item (name, description, date_purchased, date_sold, purchase_price, sale_price, category, where_sold, storage_location, photos_taken, created_at, id, user_id)
      values
      (
        ${item.name},
        ${item.description},
        ${item.date_purchased},
        ${item.date_sold},
        ${item.purchase_price},
        ${item.sale_price},
        ${item.category},
        ${item.where_sold},
        ${item.storage_location},
        ${item.photos_taken},
        ${Date.now},
        ${ItemId.random},
        ${userId.id}
      );
    """.update
      .withUniqueGeneratedKeys[Item](
        "name",
        "description",
        "date_purchased",
        "date_sold",
        "purchase_price",
        "sale_price",
        "category",
        "where_sold",
        "storage_location",
        "photos_taken",
        "id"
      )
      .transact(XA)

  def update[F[_]: Sync](item: Item, userId: UserId)(implicit XA: Transactor[F]): F[Item] =
    sql"""
      update inventory_tracker_item set
        name = ${item.name},
        description = ${item.description},
        date_purchased = ${item.date_purchased},
        date_sold = ${item.date_sold},
        purchase_price = ${item.purchase_price},
        sale_price = ${item.sale_price},
        category = ${item.category},
        where_sold = ${item.where_sold},
        storage_location = ${item.storage_location},
        photos_taken = ${item.photos_taken},
        updated_at = ${Date.now}
      where id = ${item.id.map(_.value)}
      and user_id = ${userId.id}
    """.update
      .withUniqueGeneratedKeys[Item](
        "name",
        "description",
        "date_purchased",
        "date_sold",
        "purchase_price",
        "sale_price",
        "category",
        "where_sold",
        "storage_location",
        "photos_taken",
        "id"
      )
      .transact(XA)

  def destroy[F[_]: Sync](id: Option[ItemId], userId: UserId)(implicit XA: Transactor[F]): F[Int] =
    sql"""delete from inventory_tracker_item where id = ${id} and user_id = ${userId.id}""".update.run.transact(XA)
}

trait ItemCodec {
  implicit val decoder: Decoder[Item] = deriveDecoder
  implicit val encoder: Encoder[Item] = deriveEncoder
}
