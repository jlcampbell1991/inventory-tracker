package me.josh.campbell.inventory.tracker

import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
// import org.http4s.Header
import org.http4s.implicits._
import org.http4s.twirl._

final class ItemApiRoutesTest extends BaseTest {
  import DBDriver._
  val user: User = User(java.util.UUID.randomUUID.toString, Password("pass123"), None).save[IO].unsafeRunSync
  val session: Session = Session(user.name, user.password)
  val cookie: Option[String] = Session.cookie(user).map(_.content)
  val item: Item = Item(
    name = "name",
    description = "description",
    date_purchased = Date.now,
    date_sold = None,
    purchase_price = 6.00,
    sale_price = None,
    category = "category",
    where_sold = None,
    storage_location = "storage_location",
    photos_taken = true,
    createdAt = None,
    updatedAt = None,
    id = None,
    userId = None
  ).save[IO](user.userId.get).unsafeRunSync

  val destroyableItem: Item = item.copy(name = "destroy me!").save[IO](user.userId.get).unsafeRunSync

  """POST -> Root / "api" / "v1" / "item" / "create"""" in {
    check[String](
      service.orNotFound
        .run(
          Request[IO](method = Method.POST, uri = Uri.uri("/api/v1/item/create"))
            .withEntity(
              item
            )
            .withHeaders(
              Header("auth_token", cookie.get)
            )
        ),
      Status.Ok,
      None
    )
  }
  """GET -> Root / "api" / "v1" / "item" / id""" in {
    check[Item](
      service.orNotFound
        .run(
          Request[IO](method = Method.GET, uri = Uri.unsafeFromString(s"/api/v1/item/${item.id.get.value.toString}"))
            .withHeaders(
              Header("auth_token", cookie.get)
            )
        ),
      Status.Ok,
      Some(item)
    )
  }
  """POST -> Root / "api" / "v1" / "item" / id / "update"""" in {
    check[String](
      service.orNotFound
        .run(
          Request[IO](
            method = Method.POST,
            uri = Uri.unsafeFromString(s"/api/v1/item/${item.id.get.value.toString}/update")
          ).withEntity(
              item.copy(name = "updated name")
            )
            .withHeaders(
              Header("auth_token", cookie.get)
            )
        ),
      Status.Ok,
      None
    )
  }
  """GET -> Root / "api" / "v1" / "item" / id / "destroy"""" in {
    check[String](
      service.orNotFound
        .run(
          Request[IO](
            method = Method.GET,
            uri = Uri.unsafeFromString(s"/api/v1/item/${destroyableItem.id.get.value.toString}/destroy")
          ).withHeaders(
            Header("auth_token", cookie.get)
          )
        ),
      Status.Ok,
      Some("Item ${destroyableItem.id.get.value.toString} destroyed")
    )
  }
}
