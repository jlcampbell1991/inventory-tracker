package me.josh.campbell.inventory.tracker

import doobie._
import doobie.implicits._
import cats.effect._

trait Table {
  def initialize: Update0
  def update: Update0
}

object DB {
  def tables: List[Table] = List(UserTable) ::: List(ItemTable) ::: List(PostTable)
  def initialize(T: Transactor[IO]) = tables.map(_.initialize.run.transact(T))
  def update(T: Transactor[IO]) = tables.map(_.update.run.transact(T))
}
