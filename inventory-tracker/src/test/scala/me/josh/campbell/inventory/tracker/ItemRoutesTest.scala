package me.josh.campbell.inventory.tracker

import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import org.http4s.twirl._
import org.http4s.UrlForm

final class ItemRoutesTest extends BaseTest {
  import DBDriver.XA

  val userId: UserId = UserId.random

  val user: User = User(
    "36a64d16-804b-4102-be32-3f968ff4557d",
    Password("ba04fe02-a6f7-4dad-8142-6b6a232502fc"),
    userId
  ).save[IO].unsafeRunSync

  val cookie: RequestCookie = Session.requestCookie(user)

  val itemForm: UrlForm = UrlForm(
    ("name", "97ec2260-7428-4cc6-8caa-2683c92d1e46"),
    ("description", "556f9578-55aa-49be-a958-a08180a1421e"),
    ("date_purchased", Date.now.getFormValue),
    // ("date_sold", "1bc6fb65-fbf8-4beb-9856-a6548d9fcc94"),
    ("purchase_price", "1.0"),
    // ("sale_price", "1.0"),
    ("category", "3fc01130-db6d-4543-932c-5e0a093e0c3b"),
    // ("where_sold", "0e741ed8-8201-4fd9-a15b-771563b0d16c"),
    ("storage_location", "dde21a7f-d55c-4610-b7f1-b763b12437b1"),
    ("photos_taken", "true")
  )

  val item: Item = Item(
    "7ccd0c1f-11ac-4a2d-ad52-e555b9d6aaaa",
    "2ad696df-51d0-4fcf-8440-ab15f0323cf5",
    Date.now,
    Some(Date.now),
    1.0,
    Some(1.0),
    "604bff75-d359-4574-8d73-a8db042e19ee",
    Some("b8b6a11e-4343-41bb-8593-3a636a000eee"),
    "e1ed901b-9e0c-499c-9e58-e207905320a3",
    true,
    None,
    None,
    Some(ItemId.random),
    Some(userId)
  ).save[IO](userId).unsafeRunSync

  """GET -> Root"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.GET, uri = Uri.unsafeFromString(Item.indexUrl)).addCookie(cookie)
        ),
      Status.Ok,
      None
    )
  }
  """GET -> Root / "item/ id"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.GET, uri = Uri.unsafeFromString(item.showUrl)).addCookie(cookie)
        ),
      Status.Ok,
      None
    )
  }
  """GET -> Root / "item" / "add"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.GET, uri = Uri.unsafeFromString(Item.addUrl)).addCookie(cookie)
        ),
      Status.Ok,
      None
    )
  }
  """POST -> Root / "item" / "create"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.POST, uri = Uri.unsafeFromString(Item.createUrl))
            .addCookie(cookie)
            .withEntity(
              itemForm
            )
        ),
      Status.SeeOther,
      None
    )
  }
  """GET -> Root / "item" / id / "edit"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.GET, uri = Uri.unsafeFromString(item.editUrl)).addCookie(cookie)
        ),
      Status.Ok,
      None
    )
  }
  """POST  -> Root / "item" / id / "update"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.POST, uri = Uri.unsafeFromString(item.updateUrl))
            .addCookie(cookie)
            .withEntity(
              itemForm
            )
        ),
      Status.SeeOther,
      None
    )
  }
  """GET -> Root / "item" / id / "destroy"""" in {
    check[String](
      service.orNotFound
        .run(
          Request(method = Method.GET, uri = Uri.unsafeFromString(item.destroyUrl))
            .addCookie(cookie)
            .withEntity(
              itemForm
            )
        ),
      Status.SeeOther,
      None
    )
  }
}
