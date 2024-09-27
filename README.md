# Connection-Pool Library

Welcome to my Connection Pool Library. This project demonstrates my own implementation of a database connection pool in Java. 
The connection pool manages multiple database connections, optimizing their usage and improving application performance.


## Project Structure
The project is divided into several key components:

ConnectionPool: Manages database connections, maintaining a minimum and maximum number of connections in the pool.

DatabaseConnection: Facilitates establishing connections to the database.


## Project Overview
The library provides a robust mechanism for managing database connections, ensuring efficient use of resources and maintaining optimal performance. 
Key features include:

Connection Initialization: Initializes a pool of database connections at application startup.

Connection Allocation: Manages the allocation and deallocation of connections based on demand, ensuring that the number of active connections remains within defined limits.

Error Handling: Handles connection errors by removing faulty connections and replacing them with new ones if needed.

Idle Connection Management: Regularly checks and removes idle connections that exceed the minimum pool size, freeing up resources.

Concurrency Control: Utilizes semaphores and locks to ensure thread-safe access to connections.


## Testing
The project includes unit and performance tests to validate functionality and efficiency:

Unit Tests: Verify basic operations of ConnectionPool and DatabaseConnection.

Performance Tests: Test stability and performance under load using multiple connection strategies.


## How to Run

To run the Connection Pool library, ensure you have the Java Development Kit (JDK) installed on your system. 
Follow these steps:

### Clone this repository to your computer:
<https://github.com/jakubBone/Connection-Pool>

### Navigate to the project directory

### Compile and run the ConnectionPoolTester



## Requirements
To compile and run the application, you'll need Java Development Kit (JDK) and PostgreSQL database



## Additional Information
PostgreSQL: Database used for storing connection information.

Log4j2: Logging framework for tracking application behavior.

JUnit: Library used for unit testing.
