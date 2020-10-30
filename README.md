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

## Endpoints
* `POST /api/v1/signup` to create a user and return user token
  * `curl -X POST -H "Content-Type: application/json" -d '{"name": "jlcampbell1991@gmail.com", "unencPass": "password"}' localhost:8080/api/v1/signup`
* `POST /api/v1/login` to return a user token
  * `curl -X POST -H "Content-Type: application/json" -d '{"username": "jlcampbell1991@gmail.com", "password": "password"}' localhost:8080/api/v1/login`
* `GET /api/v1/item/{id}` to retrieve an item by id
  * `curl -H "Content-Type: application/json" -H "auth_token: 0ec0b5f827e79197984394073d3768a1b22027f6-1604091056-7b91f3cb-64bc-484d-bf05-4a1f151dee00" localhost:8080/api/v1/item/5abc8fcf-3412-483d-9de4-21bdfa63d59d`
* `POST /api/vi/item` to create an item
  * `curl -X POST -H "Content-Type: application/json" -H "auth_token: 0ec0b5f827e79197984394073d3768a1b22027f6-1604091056-7b91f3cb-64bc-484d-bf05-4a1f151dee00" -d '{"name": "String", "description": "String", "date_purchased": "2020-10-30T15:37:27.830", "purchase_price": 6.0, "category": "String", "storage_location": "String", "photos_taken": true}' localhost:8080/api/v1/item/create`

### Example deployment
* https://campbell-inventory.herokuapp.com/ (using free Heroku dynamo, so initial start will be slow)
