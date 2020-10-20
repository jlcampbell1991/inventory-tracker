package me.josh.campbell.inventory.tracker

import Date._
import cats.implicits._
import cats.effect.Sync
import doobie._
import doobie.implicits._
import org.http4s._
import org.http4s.UrlForm
import play.twirl.api.Html
import java.util.UUID

final case class ItemId(value: UUID)
object ItemId {
  def apply(id: String): ItemId = ItemId(UUID.fromString(id))
  def random: ItemId = ItemId(UUID.randomUUID)
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
    createdAt: Option[Date],
    updatedAt: Option[Date],
    id: Option[ItemId],
    userId: Option[UserId]
) {
  def save[F[_]: Sync: Transactor](userId: UserId): F[Item] = Item.create[F](this, userId)

  def update[F[_]: Sync: Transactor](userId: UserId): F[Item] = Item.update[F](this, userId)

  def destroy[F[_]: Sync: Transactor](userId: UserId): F[Int] = Item.destroy[F](this.id, userId)

  def show: Html = Item.show(this)

  def showUrl: String = Item.showUrl(this.id)

  def edit(posts: List[Post]): Html = Item.edit(this, posts)

  def editUrl: String = Item.editUrl(this.id)

  def updateUrl: String = Item.updateUrl(this.id)

  def destroyUrl: String = Item.destroyUrl(this.id)

  private def formatCurrency(double: Double): String =
    "$" + f"$double%1.2f"

  def formattedPurchasePrice: String =
    formatCurrency(purchase_price)

  def formattedSalePrice: Option[String] =
    sale_price.map(formatCurrency(_))
}

object Item extends Model with ItemQueries with ItemViews {

  def fromUrlForm[F[_]: Sync](form: UrlForm): F[Item] =
    for {
      name <- getValueOrRaiseError[F](form, "name")
      description <- getValueOrRaiseError[F](form, "description")
      date_purchased <- getValueOrRaiseError[F](form, "date_purchased")
      date_sold <- getOptionalValue[F](form, "date_sold")
      purchase_price <- getValueOrRaiseError[F](form, "purchase_price")
      sale_price <- getOptionalValue[F](form, "sale_price")
      category <- getValueOrRaiseError[F](form, "category")
      where_sold <- getOptionalValue[F](form, "where_sold")
      storage_location <- getValueOrRaiseError[F](form, "storage_location")
      photos_taken <- getBooleanOrRaiseError[F](form, "photos_taken")
    } yield Item(
      name,
      description,
      Date.fromForm(date_purchased).getOrElse(Date.now),
      date_sold.flatMap(Date.fromForm(_)),
      purchase_price.toDouble,
      sale_price.map(_.toDouble),
      category,
      where_sold,
      storage_location,
      photos_taken,
      None,
      None,
      None,
      None
    )
}

trait ItemQueries extends Queries {
  def all[F[_]: Sync](userId: UserId)(implicit XA: Transactor[F]): F[List[Item]] =
    sql"""
      select * from inventory_tracker_item where user_id = ${userId}
    """.query[Item].to[List].transact(XA)

  def find[F[_]: Sync](itemId: ItemId, userId: UserId)(implicit XA: Transactor[F]): F[Item] =
    sql"""
     select * from inventory_tracker_item where id = ${itemId.value} and user_id = ${userId.id}
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
        "created_at",
        "updated_at",
        "id",
        "user_id"
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
        "created_at",
        "updated_at",
        "id",
        "user_id"
      )
      .transact(XA)

  def destroy[F[_]: Sync](id: Option[ItemId], userId: UserId)(implicit XA: Transactor[F]): F[Int] =
    sql"""delete from inventory_tracker_item where id = ${id} and user_id = ${userId.id}""".update.run.transact(XA)
}

trait ItemViews extends Views {
  def default: String = indexUrl

  def index(items: List[Item]): Html = views.html.item.index(items)

  def indexUrl: String = s"""/"""

  def show(item: Item): Html = views.html.item.show(item)

  def showUrl(maybeId: Option[ItemId]): String =
    getUrlOrDefault[ItemId](maybeId, id => s"""/item/${id.value.toString}""")

  def add: Html = views.html.item.add()

  def addUrl: String = s"""/item/add"""

  def createUrl: String = s"""/item/create"""

  def edit(item: Item, posts: List[Post]): Html = views.html.item.edit(item, posts)

  def editUrl(maybeId: Option[ItemId]): String =
    getUrlOrDefault[ItemId](maybeId, id => s"""/item/${id.value.toString}/edit""")

  def updateUrl(maybeId: Option[ItemId]): String =
    getUrlOrDefault[ItemId](maybeId, id => s"""/item/${id.value.toString}/update""")

  def destroyUrl(maybeId: Option[ItemId]): String =
    getUrlOrDefault[ItemId](maybeId, id => s"""/item/${id.value.toString}/destroy""")
}
