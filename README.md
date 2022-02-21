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

## Basic workflow
1. Submit order with `POST http://localhost:8081/orders`
2. The order is processed and send to the store through **kafka**
   1. If the store has the products available in stock, it will complete the order and return success through kafka again
   2. If the products are not available, the order will be saved into the database until the products are loaded. Use:
      1. `GET http://localhost:8080/products` to see all products in store
      2. `GET http://localhost:8080/products/requested` to see the amount and type of products required to complete pending orders
      3. `POST http://localhost:8080/products` to load new and already existing products
3. Once the order is completed, successful result will be sent back to the shop and the order status will be updated into the database and UI