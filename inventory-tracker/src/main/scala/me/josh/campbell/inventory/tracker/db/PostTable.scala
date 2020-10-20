package me.josh.campbell.inventory.tracker

import doobie._
import doobie.implicits._

object PostTable extends Table {
  def initialize: Update0 = sql"""
    DROP TABLE IF EXISTS inventory_tracker_post;
    CREATE TABLE inventory_tracker_post(
      outlet VARCHAR,
      link VARCHAR,
      item_id VARCHAR,
      created_at TIMESTAMP,
      updated_at TIMESTAMP,
      id VARCHAR PRIMARY KEY,
      user_id VARCHAR
    )""".update

  def update: Update0 =
    sql"""DROP TABLE IF EXISTS inventory_tracker_post""".update
}
