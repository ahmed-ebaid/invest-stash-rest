# Users Service
A RESTful web application that supports user creation and retrieval from a MySQL database. For this project the following end points are supported:
`
GET /v1/users
POST /v1/users
`
## Getting Started
To get started with this project, you can either:
1. Clone the project via: `git clone https://github.com/ahmed-ebaid/invest-stash-rest.git`
2. download a zip file from https://-github.com/ahmed-ebaid/invest-stash-rest
* Import the project into eclipse.

## Prerequisites
The following software is needed to complete this project:
1. Eclipse IDE
2. MySQL
3. Gradle
4. Java JDK. I'm using 1.8.0_45
5. Tomcat 8

Replace the following fields in DBConstants.java with their appropriate values:
1. **DB_NAME** -> MySQL database name.
2. **DB_USERS_TABLE** ->  MySql table name containing users.
3. **DB_USER** ->  MySQL username
4. **DB_PASSWORD** ->  MySQL password for DB_USER

## Installing
To generate files needed by eclipse to compile the project, run the following command:
* `gradle eclipse`

## Testing
To run unit tests, run the following command:
* `gradle test`

### Retrieving Users
An output from a curl request could be one of the following:
1. 500 Server Error caused by server issue.
2. 422 Unprocessable Entity which can be due to an invalid query.
3. 200 OK which is a result of a successful retrieved of users from the database.

* The following query is a GET request for retrieving users with metadata matching **age 32**. query can either match email, full_name or metadata. An invalid query can be one that is empty or one that exceeds 2000 characters.
`curl -H "Content-Type: application/json" -X GET "http://localhost:8080/invest-stash-rest/rest/v1/users?query=age%2032"`
* The following query is a GET request for retrieving all users in the database.
 `curl -H "Content-Type: application/json" -X GET "http://localhost:8080/invest-stash-rest/rest/v1/users"`

### Adding a User
An output from a curl request could be one of the follwing:
1. 201 Created which is a result of a successful retrieved of users from the database.
2. 422 Unprocessable Entity which can be due to an invalid query. Additional json errors should be displayed to indicate the reason of failure (i.e, password is empty, phone number too long)
3. 400 Bad Request which is a result of passing in a parameter to the post request other than email, phone_number, full_name, password, and metadata.
4. 500 Server Error caused by server issue.

* The following query is a POST request for adding a user to the database. Output of this query is the user that just got added.
`curl -i -H "Content-Type: application/json" -X POST "http://localhost:8080/invest-stash-rest/rest/v1/users" -d "{\"email\":\"user@example.com\",\"phoneNumber\":\"1234567891\",\"fullName\":\"Ahmed Ebaid\",\"password\":\"asdfg\",\"metadata\":\"age 20, educated\"}"`

## Built With
Maven - Dependency Management

## Authors
Ahmed Ebaid

## License
This project is licensed under the MIT License - see the LICENSE.md file for details
