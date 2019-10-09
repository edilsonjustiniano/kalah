# Kalah game


## Game's origin
Kalah, also called Kalaha or Mancala, is a game in the mancala family imported in the United States by William Julius Champion, Jr. in 1940. 
This game is sometimes also called "Kalahari", possibly by false etymology from the Kalahari desert in Namibia.


## Kalah's rule
Each of the two players has ​**​six pits​** ​in front of him/her. To the right of the six pits, each player has a larger pit, his Kalah or house.

At the start of the game, six stones are put in each pit.

The player who begins picks up all the stones in any of their own pits, and sows the stones on to the right, one in each of the following pits, 
including his own Kalah. No stones are put in the opponent's' Kalah. If the players last stone lands in his own Kalah, he gets another turn. 
This can be repeated any number of times before it's the other player's turn.

When the last stone lands in an own empty pit, the player captures this stone and all stones in the opposite pit (the other players' pit)
and puts them in his own Kalah.

The game is over as soon as one of the sides run out of stones. The player who still has stones in his/her
pits keeps them and puts them in his/hers Kalah. The winner of the game is the player who has the most stones in his Kalah.

This repository contains a application to simulate the 6 stone kalah game.

It is being developed using Java 8, Spring Boot, Spring Mongo Data, Lombok and gradle.


## Solution

To implement the game according its rule, it was created a Spring Boot RESTfull API. using the following the following technologies:

- Java 8
- MongoDB
- Spring Data MongoDB
- Spring DI
- Spring Boot
- RESTfull API
- Docker
- Docker-compose
- Gradle

In this repository you will going to see a program highly scalable, testable, readable and maintainable. For those reason
the project is easily deployed since it is using some of the best approach and technologies in the market.

I used as principle the `Clean Architecture` as most as possible. It was not complete `Clean Architecture` because I used a Spring Boot and
MongoDB, but if you have a look deeply on this repository you will be able to see that no extra knowledge is required to test the application.

I also used as principle `KISS` (Keep it simple, stupid).

The project structure and the responsibilities are stated below:

`api` is the package that contains the REST API definition for the game domain. 
It is split in the following packages: `controller`, `exception`, `model`, `service`.

- On `controller` sub-package it is present the class that define the RESTfull API.
- On `exception` sub-package there is a ExceptionHandler for the Game API domain exception, also
I have the custom exception and the error code as well.
- On `model` sub-package you can find out the class `GameResponse` used by the Game API as an entity response.
- On `service` sub-package, there are three different classes each one has their single responsibility. 
The `GameBuilder` class contains the necessary logic to build any kind of object instance regarding of the Game API.
The `GameDataValidator` class is responsible to perform the data validation such as the game is still running or not, the movement is a valid or not, etc.
The `GameService` class is the service class that receives all the requests that come from the Rest Controller. So, It is responsible
to handle the game interactions.

`configuration` is the package that contains the custom configuration required for the application. One of them is
the `SwaggerConfig` to document the REST API. The other configuration is the Jackson object mapper. 

`persistence`: is the package that contains the entity `Game` as the main entity of the application. Also on this package there is
a sub-package called `repository` that contains an interface called **GameRepository** used as a database abstraction layer.

I decided to use the SpringData to access the database methods because it is quite simple and fast to create a new query on database, 
it is only required a new method signature, sometimes a `@Query` definition.

`exception` is the package that is responsible to expose the `BaseException` and also the application `BaseError` class. 
This last one was created to customize the application error messages.

Inside the `BaseError` there are two attributes, message and errorCode. The errorCode contains a String error key that can be used by the
Front-end or even other micro-service application to map the error and present a message according the language desired. An example of `BaseError`:

````json
{
  "message": "The game was not found.",
  "errorCode": "game.notfound"
}
````  

To define a new error key I did use the same pattern used to create application properties. 
`application.context.reason`

## Design patterns

- Tell don't ask
- Singleton for bean instantiation
- Builder

## Prerequisites

It is necessary the followings to build the program:
- Latest docker version
- docker-compose
- Latest gradle version
- JDK 1.8+

To run the program, it is required:
- JRE 1.8+
- docker
- docker-compose


## How to run

To build the application execute the following command on terminal:

```
gradle clean build
```

To run the application execute the following command on terminal:

```
docker-compose up
```

## How to test

You can use the following rest calls to validate the application:

> Create a game

```
curl --header "Content-Type: application/json" \ 
     --request POST \ 
     localhost:8080/games
```

> Retrieve a game

```
curl --header "Content-Type: application/json" \ 
     --request GET \ 
     localhost:8080/games/<gameId>
```

> Make a movement

```
curl --header "Content-Type: application/json" \ 
     --request PUT \ 
     localhost:8080/games/<gameId>/pits/<pitId>
```

## Documentation

Besides this documentation, the RESTfull API is also documented by SWAGGER, to check out 
the SWAGGER documentation, run the application and access the following link: [API documentation](http://localhost:8080/swagger-ui.html)

## Future improvements

Improve the game movement logic to avoid changes over objects by reference. I mean, instead of send as parameter the `Board` instance and change it internally without expose this change, create methods that change it internally but return the same changed instance.  
So it will be clear to everybody that some changes will be done in this object.