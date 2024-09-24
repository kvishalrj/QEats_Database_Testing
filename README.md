# Food Delivery App Database Testing - QEats

## Overview
This project involves testing the **QEats Database** across the **QA** and **STAGE** environments to ensure that the database schema and data validation are correctly implemented. The goal is to verify that the database used in the food delivery application conforms to the expected schema and adheres to data integrity constraints.

### Project Highlights:
- Validated database schema conformity across environments.
- Wrote and executed test cases to check data consistency and integrity within the **QEats** database.
- Ensured correct foreign key relationships and constraints between orders and restaurants.

## Scope of Work:
1. **Table Count Validation**: Verified the number of existing tables in the database.
2. **Order Entry Validation**: Checked database entries after placing an order.
3. **Order Deletion Validation**: Verified if deleting an order is allowed and validated the enforcement of **foreign key constraints**.
4. **Restaurant ID Validation**: Ensured that the `restaurant_id` in the **Order DB** corresponds to a valid restaurant in the **Restaurant DB**.
5. **Invalid Order Validation**: Verified that placing an order with an invalid `restaurant_id` fails as expected.

## Skills Used:
- **JDBC**
- **SQL**
- **Database Schema Validation**
- **Test Case Design**

## Tools and Technologies:
- **JDBC**: For interacting with the database.
- **SQL**: For querying and validating the database content.
- **Postman/Swagger** (optional): Used to trigger API requests for order placements, deletions, etc., and validate corresponding database entries.

## Project Structure:
1. **Test Cases**: Includes SQL queries and JDBC code that validate the schema, foreign key constraints, and order placement/deletion functionalities.
2. **Environment Setup**: Scripts for connecting to the QA and STAGE environments of the database.
3. **Test Reports**: Provides detailed logs and reports of the executed tests, including the success and failure cases.

## How to Run:
1. Clone the repository.
2. Ensure the **JDBC** drivers and connection settings are correctly configured for accessing the database in the required environment.
3. Run the **SQL queries** or **JDBC-based test scripts** to validate the database.

### Example of Running a JDBC Test:
1. Setup the **JDBC Connection** using a properties file or environment variables.
2. Run the SQL test cases using the provided JDBC test scripts.

### Example Command:
```java
java -cp jdbc-connector.jar DatabaseValidation.java
```

## Reporting:
After the execution of tests, a summary report is generated outlining:
- The tables and entries that were validated.
- Details on foreign key validation.
- The outcome of the order placement and deletion test cases.
