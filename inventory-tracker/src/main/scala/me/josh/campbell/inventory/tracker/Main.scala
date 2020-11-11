package me.josh.campbell.inventory.tracker

import cats.effect.{ExitCode, IO, IOApp}
import DBDriver.XA

object Main extends IOApp {
  def run(args: List[String]) =
    SetupServer.stream[IO].compile.drain.as(ExitCode.Success)
}
