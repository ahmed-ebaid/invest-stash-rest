# Users Service
A RESTful web application that supports user creation and retrieval from a MySQL database. For this project the following end points are supported:


`GET /v1/users`

`POST /v1/users`

## Getting Started
Import the project into Eclipse IDE after cloning or downloading the project via:
1. Clone the project via: `git clone https://github.com/ahmed-ebaid/invest-stash-rest.git`
2. download a zip file from https://github.com/ahmed-ebaid/invest-stash-rest

## Prerequisites
The following software is needed to complete this project:
1. Eclipse IDE
2. MySQL
3. Gradle
4. Java JDK. For this project, I'm using 1.8.0_45
5. Tomcat 8

Replace the following fields in DBConstants.java with their appropriate values:
1. **DB_NAME** -> MySQL database name.
2. **DB_USERS_TABLE** ->  MySql table name containing users.
3. **DB_USER** ->  MySQL username
4. **DB_PASSWORD** ->  MySQL password for DB_USER

File logging is enabled for this project. To use logging, modify the following file @
`/src/main/resources/log4j.properties` by replacing the file path @ `log4j.appender.file.File` with the approperiate destination.

## Installing
To generate files needed by eclipse, run the following gradle command:
* `gradle eclipse`

## Testing
To run unit tests, run the following gradle command:
* `gradle test`

### Retrieving Users
An output from a curl request could be one of the following:
1. 200 OK which is a result of a successful retrieval of users from the database.
2. 422 Unprocessable Entity due to an invalid query.
3. 500 Server Error caused by server or connection related issues.

The following query is a GET request for retrieving users with metadata matching **age 32**. query can either match email, full_name or metadata. An invalid query can be one that is empty or one that exceeds 2000 characters.

`curl -H "Content-Type: application/json" -X GET "http://localhost:8080/invest-stash-rest/rest/v1/users?query=age%2032"`

The following query is a GET request for retrieving all users in the database.

 `curl -H "Content-Type: application/json" -X GET "http://localhost:8080/invest-stash-rest/rest/v1/users"`

### Adding a User
An output from a curl request could be one of the follwing:
1. 201 Created which is due to a user being successfully added to the database.
2. 422 Unprocessable Entity which is due to an invalid query. Additional json errors should be displayed to indicate the reason of failure (i.e, password is empty, phone number too long)
3. 400 Bad Request which is a result of passing in a parameter to the post request in addition to the following email, phone_number, full_name, password, and metadata.
4. 500 Server Error caused by server or connection related issues.

The following query is a POST request for adding a user to the database. Output of this query is the user that just got added.

`curl -i -H "Content-Type: application/json" -X POST "http://localhost:8080/invest-stash-rest/rest/v1/users" -d "{\"email\":\"user@example.com\",\"phoneNumber\":\"1234567891\",\"fullName\":\"Ahmed Ebaid\",\"password\":\"asdfg\",\"metadata\":\"age 20, educated\"}"`

## Built With
Maven - Dependency Management

## Authors
Ahmed Ebaid

## License
This project is licensed under the MIT License - see the LICENSE.md file for details
