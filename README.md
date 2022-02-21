# Build with
* Java
* Spring Boot
* Postgresql
* Hibernate
* Kafka
* Docker

## Run project
* Clone project
* ``cd`` into the project root directory and execute ``mvnw clean install -DskipTests``
* From the same directory execute ``docker-compose run -d``

Give it a few seconds for all services to start.

## Usage
The easiest way to start is by importing the postman collection under `/postman/*.json`

A simple UI tracking order statuses can be found on:
``
http://localhost:8081/orders.html
``