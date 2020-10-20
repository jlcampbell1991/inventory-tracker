package me.josh.campbell.inventory.tracker

import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load

  lazy val dbName = config.getString("postgres.dbName")
  lazy val username = config.getString("postgres.username")
  lazy val password = config.getString("postgres.password")
}
