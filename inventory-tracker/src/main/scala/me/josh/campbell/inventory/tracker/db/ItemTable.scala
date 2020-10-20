package me.josh.campbell.inventory.tracker

import doobie._
import doobie.implicits._

object ItemTable extends Table {
  def initialize: Update0 = sql"""
    DROP TABLE IF EXISTS inventory_tracker_item;
    CREATE TABLE inventory_tracker_item(
      name VARCHAR,
      description VARCHAR,
      date_purchased TIMESTAMP,
      date_sold TIMESTAMP,
      purchase_price DOUBLE PRECISION,
      sale_price DOUBLE PRECISION,
      category VARCHAR,
      where_sold VARCHAR,
      storage_location VARCHAR,
      photos_taken BOOLEAN,
      created_at TIMESTAMP,
      updated_at TIMESTAMP,
      id VARCHAR PRIMARY KEY,
      user_id VARCHAR
    )""".update

  def update: Update0 =
    sql"""DROP TABLE IF EXISTS inventory_tracker_item""".update
}
