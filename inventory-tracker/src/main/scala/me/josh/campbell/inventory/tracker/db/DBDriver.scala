package me.josh.campbell.inventory.tracker

import doobie._
import doobie.util.ExecutionContexts
import cats.effect._

object DBDriver {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val development = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    s"jdbc:postgresql:${Config.dbName}",
    s"${Config.username}",
    s"${Config.password}"
  )

  val production = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    System.getenv("JDBC_DATABASE_URL"),
    System.getenv("JDBC_DATABASE_USERNAME"),
    System.getenv("JDBC_DATABASE_PASSWORD")
  )

  implicit val XA: Transactor[IO] = development
}
