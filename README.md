# Connection-Pool Library

Welcome to my Connection Pool Library. This project demonstrates my own implementation of a database connection pool in Java. 
The connection pool manages multiple database connections, optimizing their usage and improving application performance.


## Project Structure
The project is divided into several key components:

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


## How to Use

### Step 1: Set Up the Database Connection:
Before using the ConnectionPool, create an instance of the DatabaseConnection class by providing your database credentials:
> DatabaseConnection dbConnection = new DatabaseConnection("username", "password", "database_name", port_number);

### Step 2: Create a Connection Pool:
Initialize ConnectionPool by specifying the minimum and maximum number of connections and passing the DatabaseConnection instance.

### Step 3: Acquire and Use a Connection:
Use getConnection() to retrieve a connection, then perform your database operations.

### Step 4: Release the Connection:
Call releaseConnection() to return the connection back to the pool.

### Step 5: Start and Stop Scheduler (Optional):
Use startCleanupScheduler() to manage idle connections, and stopCleanupScheduler() to gracefully stop it.

### Step 6: Disconnect
When finished, call disconnect() on the DatabaseConnection instance.


## Requirements
Java Development Kit (JDK) for compiling and running the application.

PostgreSQL Database for testing and running the application.

Log4j2 for logging application behavior.

JUnit for unit testing.
