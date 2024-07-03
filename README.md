# Solidgate Tech Assignment
A Kotlin application using Spring Boot that provides a REST endpoint `POST /set-users-balance` for updating user balances based on a provided map of user IDs and balances. The application interacts with a PostgreSQL.

### Features
* Logging memory usage. We can observe that chunks processing is more efficient. See logs while you run the tests or the application.
* Logging execution time of each chunk processing.
* BDD tests with `rest-assured` and `testcontainers`.
* Clean architecture with a clear separation of concerns.
* docker-compose setup for PostgreSQL and the application.
* Kotlin coroutines for async processing of chunks.
* Idiomatic error handling with `Result`.

### Technical decisions
* Although the technical assignment requires Map<Int, Int> as input I decided to use a file as input. This is because the file with 1 million records is x3 times smaller than the same data in JSON format. The file is also easier to generate and test.
* Chunks of `10000` records are processed reading the input as a stream. This is done to avoid memory issues when processing large files. `90-120MB` memory consumption at peak is used when processing the chunk. 1 million in one go is around `450MB+`.

### Limitations
* Tests are not executed from the Docker container. This is because the testcontainers library does not support running tests from a container easily and conflicts with the Docker daemon.
* DB level crushing is not handled. If the database crashes during the processing of a chunk, the application will not be able to recover the processing state. This can be improved by implementing higher-level transactions or a rollback mechanism [see more below](#possible-improvements).


### Prerequisites
Prerequisites
* JDK 21 (corretto-21 is used)
* Docker (for PostgreSQL setup)

### Steps
1. Clone the repository:
2. Run `docker compose up -d`. This will run a PostgreSQL db instance and API on `localhost:8080`.
3. Generate test files `./gradlew seed`. Needed for testing the application.  
   This will generate three files in the `artifacts` directory.  
   Each serves a different use case.
4. Run tests `./gradlew test`.

If you want to play around with the API, you can use the following curl command:
```shell
curl -X POST http://localhost:8080/set-users-balance -H 'Content-Type: multipart/form-data' -F 'file=@artifacts/small-file.txt'
```
```shell
curl -X POST http://localhost:8080/set-users-balance -H 'Content-Type: multipart/form-data' -F 'file=@artifacts/large-file.txt'
```
```shell
curl -X POST http://localhost:8080/set-users-balance -H 'Content-Type: multipart/form-data' -F 'file=@artifacts/corrupted-file.txt'
```


## Possible Improvements
Improvements that require more time but would be beneficial:

### Processing architecture

* Higher level transactions  
  Currently, transactions are handled per chunk, but this can lead to issues if something goes wrong at the database level. To improve this, we could implement higher-level transactions or a rollback mechanism that ensures the entire operation is rolled back if any part of it fails. Or even... (see next point)
* Utilize message broker  
  ...if we change the REST endpoint to a message broker like Kafka, we can achieve better scalability and fault tolerance when processing large volumes of data.  
  Here's an approach to implement this solution:
    1. Create a Kafka topic, for example, `user-balances`.
    2. Modify the API to collect these records from somewhere and produce messages for each chunk of 10,000 user balance updates.
    3. Develop a service that will consume messages, process them, and perform batch updates to the database.

### Application, codebase, and testing

* Don't store sensitive data in the repository for obvious reasons. Use a secret manager like AWS Secrets Manager or HashiCorp Vault.
* Better isolation of the project layers by creating clearer reference boundaries to satisfy clean architecture principles. Such project structure prevents unconventional importing between clean architecture layers.
* Use a library like https://github.com/lightbend/config for type-safe configuration properties. This will help to avoid runtime errors due to typos in property names. Remove all hardcoded strings from the codebase.
* Proper SQL statements logging via PostgreSQL driver for development mode. Exposed doesn't show the actual SQL statements when batch inserting data because Postgres rewrites them with `reWriteBatchedInserts` flag.
* Add swagger, and annotate possible outcomes for different status codes and models of the request and response.
* Add a unified response type like `ApiResponse` to have consistent errors around all API endpoints. This would enforce `ErrorResponse` for all failures making clients easier to handle errors having a consistent structure for all endpoints.
* In functional tests query the database to verify the results of the operation.