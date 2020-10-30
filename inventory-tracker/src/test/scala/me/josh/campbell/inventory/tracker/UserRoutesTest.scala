// package me.josh.campbell.inventory.tracker
//
// import cats.effect.IO
// import org.http4s._
// import org.http4s.circe.CirceEntityCodec._
// import org.http4s.implicits._
// import org.http4s.twirl._
//
// final class UserRoutesTest extends BaseTest {
//   val userForm: UrlForm = UrlForm(("name", "goober"), ("password", "pass123"), ("passwordConfirmation", "pass123"))
//
//   """GET -> Root / "signup"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.GET, uri = Uri.uri("/signup"))
//         ),
//       Status.Ok,
//       None
//     )
//   }
//
//   """POST -> Root / "signup"""" in {
//     check[String](
//       service.orNotFound
//         .run(
//           Request(method = Method.POST, uri = Uri.uri("/signup")).withEntity(
//             userForm
//           )
//         ),
//       Status.SeeOther,
//       None
//     )
//   }
// }
