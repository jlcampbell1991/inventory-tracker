// package me.josh.campbell.inventory.tracker
//
// import cats.effect.IO
// import org.http4s._
// import org.http4s.circe.CirceEntityCodec._
// import org.http4s.implicits._
// import org.http4s.twirl._
// import org.http4s.UrlForm
//
// final class PostRoutesTest extends BaseTest {
//   import DBDriver.XA
//
//   val user: User = User(
//     "2ae16122-c5c5-46c2-a80e-010f214e07c2",
//     Password("bb237f41-3936-4f03-959b-79b623677d0c"),
//     None
//   ).save[IO].unsafeRunSync
//
//   val cookie: RequestCookie = Session.requestCookie(user)
//
//   val itemId = ItemId("0305ff48-5fce-4aa5-86a1-cfb42b61f845")
//
//   val postForm: UrlForm = UrlForm(
//     ("outlet", "8e74cdbc-822c-4b52-8099-3c00cb0c0fb6"),
//     ("link", "56fdbbef-d1d6-4ab1-911a-0b54b70d4e19"),
//     ("item_id", "95e3e994-cedc-445b-ad3f-cdb7686e667b")
//   )
//
//   val post: Post = Post(
//     "6eadc2e2-ec51-489e-b50c-6bcf2167d54e",
//     "f9331269-e2d5-4b69-a66c-f53db0d7e009",
//     itemId,
//     None,
//     None,
//     Some(PostId.random),
//     user.userId
//   ).save[IO](userId).unsafeRunSync
//
//   """GET -> Root / "posts"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.GET, uri = Uri.unsafeFromString(Post.indexUrl)).addCookie(cookie)
//         ),
//       Status.Ok,
//       None
//     )
//   }
//   """GET -> Root / "post/ id"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.GET, uri = Uri.unsafeFromString(post.showUrl)).addCookie(cookie)
//         ),
//       Status.Ok,
//       None
//     )
//   }
//   """GET -> Root / "post" / "add"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.GET, uri = Uri.unsafeFromString(Post.addUrl(itemId))).addCookie(cookie)
//         ),
//       Status.Ok,
//       None
//     )
//   }
//   """POST -> Root / "post" / "create"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.POST, uri = Uri.unsafeFromString(Post.createUrl(itemId)))
//             .addCookie(cookie)
//             .withEntity(
//               postForm
//             )
//         ),
//       Status.SeeOther,
//       None
//     )
//   }
//   """GET -> Root / "post" / id / "edit"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.GET, uri = Uri.unsafeFromString(post.editUrl)).addCookie(cookie)
//         ),
//       Status.Ok,
//       None
//     )
//   }
//   """POST  -> Root / "post" / id / "update"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.POST, uri = Uri.unsafeFromString(post.updateUrl))
//             .addCookie(cookie)
//             .withEntity(
//               postForm
//             )
//         ),
//       Status.SeeOther,
//       None
//     )
//   }
//   """GET -> Root / "post" / id / "destroy"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.GET, uri = Uri.unsafeFromString(post.destroyUrl))
//             .addCookie(cookie)
//             .withEntity(
//               postForm
//             )
//         ),
//       Status.SeeOther,
//       None
//     )
//   }
// }
