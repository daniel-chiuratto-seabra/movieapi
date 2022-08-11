# To Do

* Improve the request validations and its response payloads in a more standardized way, since `hibernate-validator` does
not build much friendly message by default

* Replace the `OpenAPI` annotation method to generate the API spec by externalizing the API spec (in a `yaml` file for
instance) and then generating the `Controller` classes and `DTO` classes from it using some sort of library or plugin
such as `openapi-generator-maven-plugin`, in order to implement and maintain the application under `API First`
method

* Refactor integration test code to be better readable and maintainable, also changing the `SignUp` and `SignIn` strategy
by doing it per class and not per test (end up being by test due to lack of time to restructure everything for it)

* Implement missing scenarios in the automation tests, where I did it manually locally already using `PostMan`,
but it should be made automatically

* Despite the choice of using `H2` for development, it would be good also to implement something like `Testcontainers`
to be used in the Integration Tests to trigger external databases, simulating a scenario more similar than production

* Change the `Docker` image process generation from `spring-boot:build-image` to a more lightweight one, since it takes
a while to generate it using such plugin (at least for the first time that it is run)

[**Return to main document**](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/README.md)