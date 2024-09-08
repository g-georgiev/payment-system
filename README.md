# payment-system
This project is a POC for a Payment System for processing online transactions.

## Entities

### Merchant
Merchant is the recipient of the payment. The basic CRUD functionality is provided for merchants including an endpoint 
supporting paging and sorting as well as a Spring Batch job for importing merchants from a CSV file. 
To import merchants from a CSV file:
1. Create your CSV with the following columns: **name, email, description**
2. Place it in the resources folder under: **src/main/resources/static/import/merchant/**
3. (Re)Start the project.

### Transactions

The following transaction types can be posted with the provided endpoints:
 * **Authorize transaction** - Indicates that the payment has been authorized from the client side and is awaiting processing
 * **Charge transaction** - Indicates that the payment has been processed, charged from the client and provided to the merchant
 * **Refund transaction** - Indicates that a Refund has been requested by the client for a certain charge transaction. 
   This changes the state of the associated charge and authorize transactions to REFUNDED
 * **Reversal transaction** - Invalidates an authorize transaction 

### User credentials
The developed system supports authentication and authorization via JWT. For that reason it also includes a very light 
user management system. 

TODO: This part is underdeveloped and can be further extended with more operations to be done on the users as well as extended authorization mechanism.

## Processes

### Transaction cleanup Cronjob
A cronjob is implementing to control transaction retention. All transactions older than 1 (configurable) hour are automatically removed.

## Starting the application backend

The application is fully containerized. To start it simply run `docker-compose up` or `docker-compose up --build app` (rebuild app after changes) in the root folder.
This will spin up and configure a PostgreSQL DB instance and build the application and expose it on port **8081** via HTTP.

Should you want to run the project locally from IDE as well, it will run on the standard port **8080** and it use the same DB container.

## Documentation

Swagger UI available on: http://localhost:8080/swagger-ui/index.html