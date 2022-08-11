# Solution Description

This application is a basic API, that receives requests from users, and depending on the operation to be made, it
consumes data from an external API service that provides additional data about the requested Movie title. The API
contains 5 endpoints: `/v1/signup` (for signing up), `/v1/signin` (for signing in), `/v1/movie/bestpicture` (to  request
if a given movie title earned the Oscar as Best Picture), `/v1/movie/top10` (returns a list of the 10 most rated movies,
taking the rating average into account, sorting by Box Office in descending order) and `/v1/rating` (to rate a given
movie title). In order to determine if a given Movie won the Oscar as Best Picture, the only source is in the given CSV
file, since the external Movie source API returns if a Movie won an Oscar but not if it was for Best Picture or any
other category, therefore for the `/v1/movie/bestpicture` endpoint, only Movies that are available in the CSV file as
a Movie that won the Oscar in the Best Picture category will be returned as a Best Picture winner, any other will be
returned as a non Oscar Best Picture winner since there are no reliable information coming from the external Movie
source API that sets that. For the rating operation through the `/v1/rating` endpoint, any movie can be rated giving a
rate between 0 and 10 (including 0 and 10). Regarding the CSV content loading, it was decided to import it during Spring
Boot start up process, making the application test easier than having to upload the file through an endpoint or
something similar.

[**Return to main document**](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/README.md)