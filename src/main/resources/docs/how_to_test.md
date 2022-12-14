# How to Test 

## Swagger-UI (accessible only in Development profile)

For this, the application must be running in _Development_ profile (the default profile), because Swagger-UI is disabled
when the application is run in _Production_ profile. You can access **Swagger-UI** by accessing the link
"http://localhost:8080/swagger-ui/index.html". There you are able to execute all the operations, including `SignUp` and
`SignIn`. For acquiring the JWT Token in Swagger-UI, first you have to `SignUp`, and then do a `SignIn` with the same
credentials that were used for the `SignUp`. Once you have a response from the `SignIn` request, verify in the headers
the `Authorization` header, that will contain a `JWT Token` with the `Bearer` prefix. Copy that `Token`, then go and
click in the `Authorize` button in the top part of Swagger-UI and paste it there, clicking Ok. Then done! All the
endpoints will be accessible for requests


## PostMan

In the root folder of this project you find the `movie-api-postman-collection.json` file. You can import this `JSON`
file into PostMan in order to import the endpoint collection related to this API, and therefore, test it. Just pay
attention in the placeholders that needs to be replaced like `<MOVIE-TITLE>`, `<USERNAME>` and `<PASSWORD>`, also the
`Authorization` tab needs to have the `JWT Token` set in the `Bearer Token`


## CURL

It is possible also to use `curl` to do the needed requests to use the API. Following you find a list of `curl` commands
so, they can be `copy` `pasted` for testing (just making sure to replace the placeholders accordingly):

* **User Sign Up**: `curl -v -d '{"username":"<USERNAME>", "password":"<PASSWORD>"}' -H "Content-Type: application/json" -X
POST "http://localhost:8080/v1/signup"`

* **User Sign In**: `curl -v -d '{"username":"<USERNAME>", "password":"<PASSWORD>"}' -H "Content-Type: application/json" -X
POST "http://localhost:8080/v1/signin"` (when executing this command, copy the `Authorization` header value with the
`JWT Token`, and paste it in the placeholder `<JWT-TOKEN>` in the possible commands below)

* **Rating Request**: `curl -v -d '{"movieTitle":"<MOVIE-TITLE>", "value":"<RATE-VALUE>"}' -H
"Content-Type: application/json" -H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

* **Top 10 List**: `curl -v -H "Accept: application/json" -H "Authorization: <JWT-TOKEN>" -X GET
"http://localhost:8080/v1/movie/top10"`

* **Best Picture**: `curl -v -H "Accept: application/json" -H "Authorization: <JWT-TOKEN>" -X GET
"http://localhost:8080/v1/movie/bestpicture?movieTitle=<MOVIE-TITLE>"`


### Following there is a list of `curl` commands so the database can be filled with random data (Movies and Ratings) for testing purposes

**Pay attention in replacing the <JWT-TOKEN> placeholder with a proper JWT Token (the commands below are only executable in bash based terminals)**

**Copy and paste the commands below into a bash based Terminal with the application running, and then a bunch of rates will be posted for testing**

`curl -v -d '{"movieTitle":"Titanic", "value":"8"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Titanic", "value":"6"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Titanic", "value":"7"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Back to the future", "value":"10"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Avatar", "value":"8"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Avatar", "value":"9" -H "Accept: application/json"}' -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Jurassic Park", "value":"9"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Cast Away", "value":"7"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Cast Away", "value":"8"}' -H -H "Accept: application/json" "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"Forrest Gump", "value":"10"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating" && curl -v -d '{"movieTitle":"The Beach", "value":"7"}' -H "Accept: application/json" -H "Content-Type: application/json"
-H "Authorization: <JWT-TOKEN>" -X POST "http://localhost:8080/v1/rating"`

[**Return to main document**](https://github.com/daniel-chiuratto-seabra/movieapi/blob/main/README.md)