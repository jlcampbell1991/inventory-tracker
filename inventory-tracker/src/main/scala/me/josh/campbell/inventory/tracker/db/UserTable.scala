package me.josh.campbell.inventory.tracker

import doobie._
import doobie.implicits._

object UserTable extends Table {
  def initialize: Update0 = sql"""
    DROP TABLE IF EXISTS inventory_tracker_user;
    CREATE TABLE inventory_tracker_user(
      name VARCHAR UNIQUE,
      password VARCHAR,
      id VARCHAR PRIMARY KEY
    )""".update

  def update: Update0 =
    sql"""DROP TABLE IF EXISTS inventory_tracker_user""".update
}
