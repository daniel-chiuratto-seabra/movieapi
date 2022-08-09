# How to run

**Observation**: When is mentioned *profile* in this document, it means the `-P` parameter of the `mvn` command, that
for this project can be `dev` (default value, not needed to be explicitly set) or `prod`

**Observation 2**: In order to run the application without having to wait the tests to be run, just add the parameter
`-D skipTests`

## Through IDE
By simply running the `main` method available in the
`MovieApplication` class

**Observation For Eclipse:**

The project uses `Lombok`, therefore in order to make the project compilable in `Eclipse`, `Lombok` plugin needs to be
installed by double-clicking on the `lombok.jar` file inside the `resources` folder, so it can be installed, and then
adding the `lombok.jar` file into the project's build path (if not made already)

## Through Maven using `mvn` in a terminal
It can be run by running `mvn spring-boot:run` in the source folder (where `pom.xml` file is), where it runs the
application using the `spring-boot` Maven plugin (be sure to have `mvn` set in the path)

## Through `JAR` package
It can be run by executing `mvn package`, and after accessing the `target` folder (the folder where the compiled files
are), and running `java -jar movieapi-0.0.1-SNAPSHOT.jar`

* In this topic is important to mention that `lombok.jar` is not added into the `JAR`, neither do the `docs/` folder,
and regarding the `application.yaml` and `application-prod.yaml` files, when packing in _Development_ profile, only the
`application.yaml` is copied, and when in `Production` profile, only `application-prod.yaml` is copied and renamed to
`application-yaml` inside the `JAR`

## Through `Docker` container
It can be run by creating a `Docker` image with `mvn spring-boot:build-image -Dspring-boot.build-image.imageName=
<image-name>` (*replacing the `<image-name>` placeholder with a desired name*), and running it with `docker run --name
<container-name> <image-name>`

## Observation for Production Profile!

All the mentioned ways to run the application above, can be set to run for _Production_. They all are run for
_Development_ by default, but if the option `-P prod` is set, the `Spring Boot` application will be started as if it is
in _Production_, which the following differences happen:

* The `application.yaml` file settings is ignored, the `application-prod.yaml` takes place, which expects all of its
properties to be set through `Environment Variables`

* While packing into `JAR`, only the `application-prod.yaml` is copied and renamed to `application.yaml`

* The `Swagger-UI` is `disabled`

* The `H2 Console` is `disabled`

### The following Environment Variables need to be set when in _Production_ environment

* `APPLICATION_JWT_SECRET`: The secret, Base 64 encoded to be used in the JWT Token generation process

* `APPLICATION_JWT_TOKEN_EXPIRATION`: The JWT Token expiration time in milliseconds

* `APPLICATION_JPA_HIBERNATE_DDL_AUTO`: DDL setting defining the strategy which Hibernate should follow at startup
(`none`, `validate`, `update`, `create` and `create-drop`)

* `APPLICATION_JPA_GENERATE_DDL`: Vendor-agnostic parameter to set to JPA to create the schema for other JPA based
libraries or not (`true` or `false`)

* `APPLICATION_JPA_DATASOURCE_URL`: Datasource URL related to the database to connect with

* `APPLICATION_PORT`: Setting the default port to access the API

* `APPLICATION_OMDBAPI_KEY`: OMDB API token, needed to be able to retrieve the Movie Source Data from the external Service

[**Return to main document**](https://github.com/daniel-chiuratto-seabra/movieapi)