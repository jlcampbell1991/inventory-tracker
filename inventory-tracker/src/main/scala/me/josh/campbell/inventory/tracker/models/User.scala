package me.josh.campbell.inventory.tracker

import cats.implicits._
import cats.effect.Sync
import org.http4s._
import doobie._
import doobie.implicits._
import io.circe._, io.circe.generic.semiauto._, io.circe.generic.extras._
import com.github.t3hnar.bcrypt._
import java.util.UUID

final case class UserId(id: UUID) {
  override def toString = id.toString
}
object UserId {
  def apply(id: String): UserId = UserId(UUID.fromString(id))
  def random: UserId = UserId(UUID.randomUUID)

  implicit val decoder: Decoder[UserId] = io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
  implicit val encoder: Encoder[UserId] = io.circe.generic.extras.semiauto.deriveUnwrappedEncoder
}

final case class Password(get: String) extends AnyVal
object Password {
  def encrypt(p: String): Password = Password(p.bcrypt)
  implicit val decoder: Decoder[Password] = io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
  implicit val encoder: Encoder[Password] = io.circe.generic.extras.semiauto.deriveUnwrappedEncoder
}

final case class User(name: String, unencPass: Password, userId: Option[UserId]) {
  def id: String = userId.toString
  def password: String = unencPass.get

  def save[F[_]: Sync: Transactor] =
    User.create[F](this)

  def update[F[_]: Sync: Transactor] =
    User.update[F](this)

  def destroy[F[_]: Sync: Transactor] =
    User.destroy[F](this)
}
object User extends Model with Queries with UserCodec {
  def find[F[_]: Sync](id: UserId)(implicit XA: Transactor[F]): F[User] =
    sql"""select * from inventory_tracker_user where id = ${id.toString}"""
      .query[User]
      .unique
      .transact(XA)

  def find[F[_]: Sync](name: String)(implicit XA: Transactor[F]): F[User] =
    sql"""select * from inventory_tracker_user where name = ${name}"""
      .query[User]
      .unique
      .transact(XA)

  def create[F[_]: Sync](user: User)(implicit XA: Transactor[F]): F[User] = {
    sql"""
    insert into inventory_tracker_user (name, password, id)
    values
    (
      ${user.name},
      ${user.password},
      ${UserId.random}
    )
    """.update.withUniqueGeneratedKeys[User]("name", "password", "id").transact(XA)
  }

  def update[F[_]: Sync](user: User): Update0 =
    sql"""
      update inventory_tracker_user set
        name = ${user.name},
        password = ${user.password}
      where id = \{user.userId}
      """.update

  def destroy[F[_]: Sync](user: User): Update0 =
    sql"""delete from inventory_tracker_user where id = ${user.userId}""".update
}

trait UserCodec {
  implicit val decoder: Decoder[User] = deriveDecoder
  implicit val encoder: Encoder[User] = deriveEncoder
}
