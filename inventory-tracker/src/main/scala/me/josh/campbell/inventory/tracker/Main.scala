package me.josh.campbell.inventory.tracker

import cats.effect._
import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import DBDriver.XA

object Main extends IOApp {
  def run(args: List[String]) =
    // DB.initialize(DBDriver.production).sequence.as(ExitCode.Success)
    SetupServer.stream[IO].compile.drain.as(ExitCode.Success)
}
