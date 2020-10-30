# Inventory tracker
Inventory tracker is an http4s web app for managing details like buy and sale prices and dates, storage and sale locations, etc. for inventory items.  It uses:
* http4s as the HTTP router
* doobie as the database transactor
* Twirl for rendering dynamic HTML
* Circe for serializing API endpoints for a React front-end soon to come!

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

## Notable endpoints
### HTML endpoints
* `GET /signup` to create a new user
* `GET /login` to create a new session
* `GET /` to show the index of all items
* `GET /item/add` to create a new item
### JSON API endpoints
* `POST /api/v1/signup` to create a user and return user token
  * `curl -X POST -H "Content-Type: application/json" -d '{"name": "jlcampbell1991@gmail.com", "unencPass": "password"}' localhost:8080/api/v1/signup`
* `POST /api/v1/login` to return a user token
  * `curl -X POST -H "Content-Type: application/json" -d '{"username": "jlcampbell1991@gmail.com", "password": "*****"}' localhost:8080/api/v1/login`

### Example deployment
* https://campbell-inventory.herokuapp.com/ (using free Heroku dynamo, so initial start will be slow)
