# Scale

* Externalizing the datasource, so the datasource can be a single database that will handle multiple API instances directly
requesting data (through `/v1/movie/top10` and `/v1/movie/bestpicture` endpoints), and for storing data it could have an
event bus that would handle multiple Movie API instances asynchronously requesting to store Ratings and Movie data
into the database, where since it is asynchronous, the user would not feel the delay in the "Rating Posted Successfully"
response, and the delay in requesting to see if a given Movie won the Oscar as Best Picture that were not listed as
this in the CSV File, would be relied on how much time the OMDB API would take to respond, and this would happen only in
the first time, since that from the second request for the same movie and on, the movie data would be in the database
already, tending to be a faster response
  
* Caching the Movies that earned Best Picture Oscar, since the Oscar happens once per year, those Movies could be cached
and yearly refreshed for a better performance, it can also determine an average request value, where movies that are
requested at a frequency equal to or above this value should be cached as well for a better performance

* Doing a database partitioning in order to increase performance

* Working with multiple database instances, where one is used for saving data (Primary), and once the data is saved, it
synchronizes with other database (Read Replica), in a way that the amount of requests are distributed among the database
improving performance

* Create a process that periodically generate the Top 10 list, keeping it updated, so it does not need to be generated
`on-the-fly` during a request

[**Return to main document**](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/README.md)