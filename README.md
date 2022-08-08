*I KNOW! I SHOULD NOT LEAVE THE DATA BELOW IN A README, BUT IT IS TEMPORARY!*

# Build the Docker image

## For Dev
mvn clean -D skipTests spring-boot:build-image -Dspring-boot.build-image.imageName=backbase/movie-api-dev

## For Prod
mvn clean -D skipTests spring-boot:build-image -Pprod -Dspring-boot.build-image.imageName=backbase/movie-api-prod

# Build JAR for DEV
mvn package

# Build JAR for PROD
mvn package -P prod

# The set of EXPORTS having all the Environment Variables needed to run as PROD Environment
export APPLICATION_JWT_SECRET=ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=
export APPLICATION_JWT_TOKEN_EXPIRATION=86400000
export APPLICATION_JPA_HIBERNATE_DDL_AUTO=create
export APPLICATION_JPA_GENERATE_DDL=true
export APPLICATION_JPA_DATASOURCE_URL=jdbc:h2:mem:movietestdb\;DB_CLOSE_DELAY=-1\;NON_KEYWORDS=VALUE,YEAR,USER
export APPLICATION_URL=http://localhost
export APPLICATION_PORT=8080
export APPLICATION_OMDBAPI_KEY=f06bd2c1

# To run as a Docker in PROD Environment Passing the Environment Variables
docker run --name movie-api-prod \
-e APPLICATION_JWT_SECRET=ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI= \
-e APPLICATION_JWT_TOKEN_EXPIRATION=86400000 \
-e APPLICATION_JPA_HIBERNATE_DDL_AUTO=create \
-e APPLICATION_JPA_GENERATE_DDL=true \
-e APPLICATION_JPA_DATASOURCE_URL=jdbc:h2:mem:movietestdb\;DB_CLOSE_DELAY=-1\;NON_KEYWORDS=VALUE,YEAR,USER \
-e APPLICATION_URL=http://localhost \
-e APPLICATION_PORT=8080 \
-e APPLICATION_OMDBAPI_KEY=f06bd2c1 \
movieapi:0.0.1-SNAPSHOT

# To SignIn/LogIn Using CURL
curl -v -d '{"username":"<USERNAME>", "password":"<PASSWORD>"}' -H "Content-Type: application/json" -X POST http://localhost:8080/v1/signin

