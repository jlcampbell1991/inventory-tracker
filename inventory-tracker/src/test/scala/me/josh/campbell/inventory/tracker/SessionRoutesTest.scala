// package me.josh.campbell.inventory.tracker
//
// import cats.effect.IO
// import org.http4s._
// import org.http4s.circe.CirceEntityCodec._
// import org.http4s.implicits._
// import org.http4s.twirl._
//
// final class SessionRoutesTest extends BaseTest {
//   import DBDriver._
//   val user: User = User("gruber", Password("pass123"), None).save[IO].unsafeRunSync
//   val sessionForm: UrlForm = UrlForm(("name", "gruber"), ("password", "pass123"))
//
//   """GET -> Root / "login"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.GET, uri = Uri.uri("/login"))
//         ),
//       Status.Ok,
//       None
//     )
//   }
//
//   """POST -> Root / "login"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.POST, uri = Uri.uri("/login")).withEntity(
//             sessionForm
//           )
//         ),
//       Status.SeeOther,
//       None
//     )
//   }
//
//   """GET -> Root / "logout""""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.GET, uri = Uri.uri("/logout"))
//         ),
//       Status.SeeOther,
//       None
//     )
//   }
// }
