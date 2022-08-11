Movie API
=========

## Description

This project is a result of the Backbase assessment process, where the following requirement has been asked:


### `Overview`
```
The application should Indicate whether a movie won a “Best Picture” Oscar, given a movie’s title based on this API and this CSV file that
contains winners from 1927 until 2010. It should also allow users to give a rating to movies and provide a list of 10 top-rated movies ordered by
box office value.
```


### `Solution`
```
The code and the deliverables should be production-ready
Here you have the Backbase stack: https://stackshare.io/backbase/backbase our services are built mainly using Java 11 (updating to
17), Spring, Hibernate and Maven. Your solution HAS TO be written in a supported version of java. You are free to use any or none
other frameworks if you like; we will review the proper use of any framework or library included.`
```

So, taking those definitions into account, there are the following documentation files inside the `docs` folder:

* **[solution.md](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/src/main/resources/docs/solution.md)** -
a short (min two lines, max half a page) description of the solution and explaining some design decisions

* **[how_to_run.md](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/src/main/resources/docs/how_to_run.md)** -
a short explanation about how to run the solution with all the needed parts

* **[how_to_test.md](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/src/main/resources/docs/how_to_test.md)** -
file explaining what needs to be done to use the service.

* **[to_do.md](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/src/main/resources/docs/to_do.md)** -
to-do list with things you would add if you have more time or explaining what is missing and why etc

* **[assumptions.md](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/src/main/resources/docs/assumptions.md)** -
your assumptions when solving the challenge

* **[scale.md](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/src/main/resources/docs/scale.md)** -
a description of how it will scale when the number of users/agents/consumers grows from 100 per day to 10000000 per day,
and what changes would have to be made to keep the same quality of service


## Requirements to run the application

* IDE
  * _Eclipse_ (not forgetting to install the `Lombok` plugin from the `lombok.jar` file inside the `resources` folder)
  * _IntelliJ Idea_ (does not require any plugin for `Lombok`)

* Maven 3.8.4

* Java 11.0.8+

* Docker 20+


## Used Libraries and Frameworks

* Spring Boot

* Spring Data (to allow the use of ORM functionalities with Hibernate)

* Spring Validation (for payload validation)

* Spring Doc OpenAPI UI (to provide Swagger-UI under OpenAPI `developemt profile only`)

* Spring Security (to deal with JWT Token authentication process)

* JJWT (to generate the JWT Token)

* OpenCSV (to deserialize data from the CSV File that is loaded while starting the application)

* H2 In-Memory Database (to allow easy access to database for development)

* Lombok (to deal with boilerplate code through annotations)
  
* A `GitHub` workflow file has been added (`.github/workflows/test-execution.yaml`) in order to trigger test builds when commits are pushed into repository
