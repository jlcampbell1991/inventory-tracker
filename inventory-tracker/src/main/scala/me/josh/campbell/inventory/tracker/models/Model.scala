package me.josh.campbell.inventory.tracker

// import cats.implicits._
import cats.effect.Sync
import doobie._
import io.circe._ //, io.circe.generic.semiauto._
import java.time.{LocalDate, LocalDateTime, Month}
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.http4s._
import org.http4s.UrlForm
import scala.util.Try

trait Model {
  protected def getValueOrRaiseError[F[_]: Sync](form: UrlForm, key: String): F[String] =
    form
      .getFirst(key)
      .fold(Sync[F].raiseError[String](MalformedMessageBodyFailure(s"forgot $key")))(Sync[F].pure(_))

  protected def getBooleanOrRaiseError[F[_]: Sync](form: UrlForm, key: String): F[Boolean] =
    Sync[F].pure(
      form.getFirst(key) match {
        case Some("on") => true
        case _ => false
      }
    )

  protected def getOptionalValue[F[_]: Sync](form: UrlForm, key: String): F[Option[String]] =
    Sync[F].pure(form.getFirst(key))

}

trait Queries {
  implicit val uuidGet: Get[UUID] = Get[String].map(UUID.fromString(_))
  implicit val uuidPut: Put[UUID] = Put[String].contramap(_.toString)
}

case class Date(value: LocalDateTime) {
  def getFormValue: String =
    value.format(Date.formatter)
}

object Date extends doobie.util.meta.TimeMetaInstances with doobie.util.meta.MetaConstructors {
  def apply(month: Int, day: Int, year: Int) =
    LocalDateTime.of(year, Month.of(month), day, now.value.getHour, now.value.getMinute)

  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def fromForm(value: String): Option[Date] =
    Try {
      Date(
        LocalDate.parse(value, formatter).atStartOfDay
      )
    }.toOption

  def now: Date = Date(LocalDateTime.now)

  implicit val get: Get[Date] = Get[LocalDateTime].map(Date(_))
  implicit val put: Put[Date] = Put[LocalDateTime].contramap(_.value)

  implicit val decoder: Decoder[Date] = io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
  implicit val encoder: Encoder[Date] = io.circe.generic.extras.semiauto.deriveUnwrappedEncoder

}
