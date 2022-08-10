# Assumptions

As per the defined requirement available in the [README](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/README.md)
file, the following assumptions were made:

* The [OMDB API](http://omdbapi.com) does not offer reliable information regarding the Oscar prizes per movie in a way
that is not possible to identify when a Movie won an Oscar for Best Picture, so because of that the CSV file content
will be the only source able to determine what movies won for Best Picture in the Oscar, for the others they will be
considered that they did not win for Best Picture. The reason is that the only part of the payload returned by
[OMDB API](http://omdbapi.com) is in the `awards` property, and it shows only how many oscars the Movie earned, but not
their categories

* Everytime a request for a Movie is made like, verifying if a given Movie won the Oscar as Best Picture or requesting
to rate a given Movie, the system verifies if the Movie is already stored in the database, where if not, it calls the
external Movie source API (OMDB) and store it into the database, so if next time a user request to rate or to verify if
the same Movie won a Best Picture Oscar, the API just load it from the database not needing to request the API again

* The Top 10 list should show all the rated movies, but it will be showing even non-rated as well as average 0, and as
long the Movies available in the database start to become rated, then the Top list will start to have more logic with
ordering by the rated average ones

* The chosen database is the `In-Memory H2` embedded into Spring, but it has been chosen only for development purposes,
for production an external database should be used instead, even to allow elasticity since more than one API instance
should be able to reach the same database (directly or non-directly, depending on the architecture choice)

* Despite the choice of using `H2` for development, it would be good also maybe to implement `Testcontainers` with the
Integration Tests to trigger external databases for testing purposes in a scenario more similar than production

[**Return to main document**](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/README.md)