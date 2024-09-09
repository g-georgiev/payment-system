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

## General flows
The following general flows can be followed with the existing implementation:
- As an ADMIN user you can list all existing merchants with paging and sorting supported as well as create, update and delete merchants (delete is only possible if the merchant has no transactions).
- As a MERCHANT user you can create transactions and view all of your transactions. The lifecycle of transactions is as follows:
  1. **Authorize** transaction is created by providing **an amount, customer email and phone, and the merchant ID**.
     ``` 
     POST http://localhost:8080/transaction
     {
         "transactionType": "AUTHORIZE",
         "customerEmail": "customer1@test.com",
         "customerPhone": "0123456789",
         "merchantId": 212,
         "amount": 5400.99
     }
     
     Response:
     {
         "uuid": "48c826eb-dd2a-4eec-ad80-cf6f1287d319",
         "transactionType": "AuthorizeTransaction",
         "status": "APPROVED",
         ...
     }          
  2. **Charge** transaction is created **ONLY by providing the reference UUID of an Authorize transaction**. Any other data provided **WILL BE IGNORED** and instead the data of the Authorize transaction will be used.
     **Important**: **The Authorize transaction provided must be in the APPROVED state**. Otherwise, the Charge transaction will be created in **ERROR** state and will be unusable.
     ``` 
     POST http://localhost:8080/transaction
     {
         "transactionType": "CHARGE",
         "referenceId": "48c826eb-dd2a-4eec-ad80-cf6f1287d319"
     }
     
     Response:
     {
         "uuid": "5438fb3f-6297-4411-94ed-46e22e6d87c1",
         "transactionType": "ChargeTransaction",
         "status": "APPROVED",
         ...
     }      
  3. **Refund** transaction is created **ONLY by providing the reference UUID of a Charge transaction**. Any other data provided **WILL BE IGNORED** and instead the data of the Authorize transaction will be used.
     **Important**: **The Charge transaction provided must be in the APPROVED state**. Otherwise, the Refund transaction will be created in **ERROR** state and will be unusable.
     Creating an approved Refund transaction will cause **BOTH the Charge transaction it references AND the Authorize transaction referenced by the Charge to go to status REFUNDED**.
     ``` 
     POST http://localhost:8080/transaction
     {
         "transactionType": "REFUND",
         "referenceId": "5438fb3f-6297-4411-94ed-46e22e6d87c1"
     }
     
     Response:
     {
         "uuid": "48836c65-f4f0-4e40-8be0-10a9c93ba03c",
         "transactionType": "RefundTransaction",
         "status": "APPROVED",
         ...
     }      
  4. **Reversal** transaction is created **ONLY by providing the reference UUID of a Charge Authorize**. Any other data provided **WILL BE IGNORED** and instead the data of the Authorize transaction will be used.
     **Important**: **The Authorize transaction provided must be in the APPROVED OR REFUNDED state**. Otherwise, the Reversal transaction will be created in **ERROR** state.
     Creating an approved Reversal transaction will cause **the Authorize transaction it references to go to status REVERSED**.
     ``` 
     POST http://localhost:8080/transaction
     {
         "transactionType": "REVERSAL",
         "referenceId": "48c826eb-dd2a-4eec-ad80-cf6f1287d319"
     }
     
     Response:
     {
         "uuid": "a0a46a3e-01b3-4b4b-bc9f-ff520fdb47ef",
         "transactionType": "ReversalTransaction",
         "status": "APPROVED",
         ...
     }      

## Processes

### Transaction cleanup Cronjob
A cronjob is implementing to control transaction retention. All transactions older than 1 (configurable) hour are automatically removed.

## Starting the application backend

The application is fully containerized. To start it simply run `docker-compose up` or `docker-compose up --build app` (rebuild app after changes) in the root folder.
This will spin up and configure a PostgreSQL DB instance and build the application and expose it on port **8081** via HTTP.

Should you want to run the project locally from IDE as well, it will run on the standard port **8080** and it use the same DB container.

## Documentation

Swagger UI available on: http://localhost:8080/swagger-ui/index.html