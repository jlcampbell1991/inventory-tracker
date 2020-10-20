# Inventory tracker
Inventory tracker is an http4s web app for managing details like buy and sale prices and dates, storage and sale locations, etc. for inventory items.

## Running locally
* [Install sbt](http://www.scala-sbt.org/1.0/docs/Setup.html)
* [Install postgresql](https://www.postgresql.org/download/)
* Add `application.conf` file for your local postgres DB to `/inventory-tracker/src/main/resources/` using template below.  This file should be included in .gitignore.
* Make sure `XA` in `/inventory-tracker/src/main/scala/me/josh/campbell/inventory/tracker/db/DBDriver.scala` is defined as `development` on line 24
* Set port in `/inventory-tracker/src/main/scala/me/josh/campbell/inventory/tracker/SetupServer.scala` line 19-20

## Deploying to Heroku
* [Install sbt](http://www.scala-sbt.org/1.0/docs/Setup.html)
* Make sure `XA` in `/inventory-tracker/src/main/scala/me/josh/campbell/inventory/tracker/db/DBDriver.scala` is defined as `production` on line 24
* Set port to `.bindHttp(System.getenv("PORT").toInt, "0.0.0.0")` in `/inventory-tracker/src/main/scala/me/josh/campbell/inventory/tracker/SetupServer.scala` line 19-20
* Follow [Heroku instructions](https://devcenter.heroku.com/articles/deploying-scala) for deploying a Scala app


### application.conf template
```
postgres {
  "dbName": "your_db_name",
  "username": "your_user_name",
  "password": "your_password"
}
```
