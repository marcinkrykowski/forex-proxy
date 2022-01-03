# Forex system proxy
Example microservice written in Scala using some modern FP libraries.

*Disclaimer: This project serves as piece of my code. In production code there would be quite a few changes.*

#### Table of Contents
* [Use case](#use-case)
* [How to run application locally](#how-to-run-application-locally)
* [Endpoints](#endpoints)
* [Example request](#example-request)
* [Configuration](#configuration)
* [Tests](#tests)
* [Assumptions](#assumptions)
* [Why I used http4s (library) over for example Play (framework)?](#why-i-used-http4s-library-over-for-example-play-framework)
* [Possible improvements](#possible-improvements)

## Use case
The use case in this system is quite simple. We want to use configurable forex system proxy to be independent of the underlying system that keep the actual values.
We might use one system today and decide that we want to change it tomorrow. We don't 

## How to run application locally
As this project is a `sbt` application you can either import it to your IDE fo choice e.g. IntelliJ or execute `sbt run` from the rot folder.
By default, the app serves traffic at `localhost:8092`.

## Endpoints
Rest endpoints listed below:

Method | Url          | Parameters                     | Description
------ | -----------  | -----------                    |  -----------
GET    | /rates       | firstCurrency, secondCurrency  | Returns latest currency rates from external source - [API](https://hub.docker.com/r/paidyinc/one-frame)

### Example request
Some example request that might be executed via curl or in browser `http://localhost:8092/rates?firstCurrency=USD&secondCurrency=JPY`

## Configuration
The application might be configured by changing `application.conf` in resources.
Things that might be changed are 
- forex system that we use (currency rates provider)
- token (its name and value)
- application config (hostname and port used by the app)

## Tests
To run tests execute `sbt test`. You will run all unit tests.
To run integration tests start a Docker image by doing `docker-compose up` in root. After that run `sbt it:test` or run `CurrencyServerSpec.scala` from IDE.

## Assumptions
Project has a few assumptions. The proxy does not validate whether the passed currencies are valid or not. User might pass not supported currency but the assumption is that the underlying Forex API will respond with appropriate message.

## Why I used http4s (library) over for example Play (framework)?
It is much better to have full control over what's happening in my application than rely on some runtime magic and annotations. That's why I've chosen http4s library over Play.
Might have used [tapir](https://github.com/softwaremill/tapir), [typedApi](https://github.com/pheymann/typedapi) or [akka-http](https://github.com/akka/akka-http) as well.

## Possible improvements
- [ ] Add Swagger integration for API documenting
- [ ] Add continuous integration
- [ ] Add property based testing