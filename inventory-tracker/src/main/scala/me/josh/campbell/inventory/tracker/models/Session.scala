package me.josh.campbell.inventory.tracker

import cats.effect.Sync
// import cats.implicits._
import org.http4s._
import org.http4s.{Headers, ResponseCookie}
import org.http4s.util.CaseInsensitiveString
import org.reactormonk.{CryptoBits, PrivateKey}
import doobie._
import doobie.implicits._
import com.github.t3hnar.bcrypt._
import io.circe._, io.circe.generic.semiauto._
import java.time._

case class Session(username: String, password: String) extends Queries {
  def findUser[F[_]: Sync](implicit XA: Transactor[F]): F[Option[User]] =
    sql"""select * from inventory_tracker_user where name = ${username}"""
      .query[User]
      .option
      .transact(XA)

  def auth[F[_]: Sync](user: User): Option[User] =
    if (password.isBcrypted(user.password)) Some(user)
    else None
}
object Session extends Model {
  val COOKIE_NAME = "inventory_tracker_cookie"

  private val key = PrivateKey(scala.io.Codec.toUTF8(scala.util.Random.alphanumeric.take(20).mkString("")))

  private val crypto = CryptoBits(key)

  def cookie(user: User): Option[ResponseCookie] = user.userId.map { userId =>
    ResponseCookie(name = COOKIE_NAME, content = crypto.signToken(userId.toString, Instant.now.getEpochSecond.toString))
  }

  def requestCookie(user: User): Option[RequestCookie] = user.userId.map { userId =>
    RequestCookie(name = COOKIE_NAME, content = crypto.signToken(userId.toString, Instant.now.getEpochSecond.toString))
  }

  def isLoggedIn(requestHeaders: Headers): Option[UserId] =
    for {
      token <- requestHeaders.get(CaseInsensitiveString("auth_token"))
      userId <- crypto.validateSignedToken(token.value)
    } yield UserId(userId)

  implicit val decoder: Decoder[Session] = deriveDecoder
  implicit val encoder: Encoder[Session] = deriveEncoder
}
