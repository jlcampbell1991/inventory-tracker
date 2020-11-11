package me.josh.campbell.inventory.tracker

import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Timer}
import doobie._
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware._
import org.http4s.server.middleware.Logger
import org.http4s.server.staticcontent.{resourceService, ResourceService}
import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration._

object SetupServer {
  def stream[F[_]: ConcurrentEffect: Timer: ContextShift: Transactor]: Stream[F, Nothing] = {
    // TODO: Make secure CORS policy.
    val corsConfig = CORSConfig(anyOrigin = true, anyMethod = true, allowCredentials = true, maxAge = 1.day.toSeconds)
    val assetsRoutes = resourceService[F](ResourceService.Config[F]("/assets", Blocker.liftExecutionContext(global)))
    val finalHttpApp = CORS(Logger.httpApp(true, true)(Routes.routes[F](assetsRoutes).orNotFound), corsConfig)
    for {
      exitCode <- BlazeServerBuilder[F](global)
      // Production PORT
      // .bindHttp(System.getenv("PORT").toInt, "0.0.0.0")
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
