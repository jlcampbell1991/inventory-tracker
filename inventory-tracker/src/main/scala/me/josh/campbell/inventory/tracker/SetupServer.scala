package me.josh.campbell.inventory.tracker

import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Timer}
import doobie._
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.server.staticcontent.{resourceService, ResourceService}
import scala.concurrent.ExecutionContext.global

object SetupServer {
  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F], Xa: Transactor[F]): Stream[F, Nothing] = {
    val assetsRoutes = resourceService[F](ResourceService.Config[F]("/assets", Blocker.liftExecutionContext(global)))
    val finalHttpApp = Logger.httpApp(true, true)(Routes.routes[F](assetsRoutes).orNotFound)
    for {
      exitCode <- BlazeServerBuilder[F]
      // Production PORT
      // .bindHttp(System.getenv("PORT").toInt, "0.0.0.0")
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
