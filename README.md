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
3. Import the project into eclipse.

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

To run unit tests, run the following command:
* `gradle test`

## Built With
Maven - Dependency Management

## Authors
Ahmed Ebaid

## License
This project is licensed under the MIT License - see the LICENSE.md file for details
