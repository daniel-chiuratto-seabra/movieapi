# How to Test 

## Swagger-UI

Once the application is running in _Development_ profile (the default profile), it allows the access of the API through
**Swagger-UI** by accessing the link "http://localhost:8080/swagger-ui/index.html". There you are able to execute all
the operations, including `SignUp` and `SignIn`

## PostMan

In the root folder of this project you find the `movie-api-postman-collection.json` file. You can import this `JSON`
file in order to import the collection of endpoints related to this API, and therefore, test it. Just pay attention in
the placeholders that needs to be replaced like `<MOVIE-TITLE>`, `<USERNAME>` and `<PASSWORD>`, also the
`Authorization` tab needs to have the `JWT Token` set in the `Bearer Token`

## CURL

It is possible also to use `curl` to do the needed requests to use the API. Following you find a list of `curl` commands
so, they can be `copy` `pasted` for testing (just making sure to replace the placeholders accordingly):

* **User Sign Up**: `curl -v -d '{"username":"<USERNAME>", "password":"<PASSWORD>"}' -H "Content-Type: application/json" -X
POST "http://localhost:8080/v1/signup"`

* **User Sign In**: `curl -v -d '{"username":"<USERNAME>", "password":"<PASSWORD>"}' -H "Content-Type: application/json" -X
POST "http://localhost:8080/v1/signin"`

* **Rating Request**: `curl -v -d '{"movieTitle":"<MOVIE-TITLE>", "value":"<RATE-VALUE>"}' -H
"Content-Type: application/json" -H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

* **Top 10 List**: `curl -v -H "Accept: application/json" -H "Authorization: <JWT-TOKEN>" -X GET
"http://localhost:8080/v1/movie/top10"`

* **Best Picture**: `curl -v -H "Accept: application/json" -H "Authorization: <JWT-TOKEN>" -X GET
"http://localhost:8080/v1/movie/bestpicture?movieTitle=<MOVIE-TITLE>"`

### Following there is a list of `curl` commands so the database can be filled with random data for testing purposes

**Pay attention in replacing the <JWT-TOKEN> placeholder with the proper JWT Token**

`curl -v -d '{"movieTitle":"Titanic", "value":"8"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Titanic", "value":"6"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Titanic", "value":"7"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Avatar", "value":"8"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Avatar", "value":"9"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Jurassic Park", "value":"9"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Cast Away", "value":"7"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Cast Away", "value":"8"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"Forrest Gump", "value":"10"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

`curl -v -d '{"movieTitle":"The Beach", "value":"7"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

[**Return to main document**](https://github.com/daniel-chiuratto-seabra/movieapi)