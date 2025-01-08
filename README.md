🏗️ Connection-Pool Library

The Connection-Pool library is a lightweight and efficient Java solution for managing database connection pools. 
Designed as an alternative to ready-made solutions, this project aims to provide an in-depth understanding of 
connection management and its challenges in multi-threaded environments.

Watch the [Performance Test Video](https://www.youtube.com/watch?v=FgS-QVvKML4) to see the implementation in action.


## 🎯 Features

- **Automatic Pool Initialization**: Creates a minimum number of connections at startup

- **Maximum Connection Limit**: Caps the number of concurrent connections to a configured maximum

- **Multithreading Support**: Handles multiple connection requests concurrently

- **Idle Connection Cleanup**: Automatically removes unused connections beyond the minimum limit


## 🚀 Technologies Used

- **Java 21**: Core programming language 

- **JDBC**: Interface for managing database connections

- **JUnit**: Framework for testing

- **Gradle**: Build automation and dependency management


## 📂 Project Structure

```
src
├── connection_pool             # Connection pool management logic
├── database                    # Database connection handling
├── performance_test            # Performance testing utilities
├── unit_test                   # Unit testing classes
└── utils                       # Utility tools and configurations
``` 


## 🚀 Getting Started

Follow these steps to set up and run the project:

### Prerequisites

Before you begin, ensure you have the following tools installed:
- **Java Development Kit (JDK)** 21 or higher
- **Gradle** for dependency management
- **PostgreSQL** database

### Setup Instructions

1. **Clone the Repository**  
   Download the project files to your local machine:
   ```bash
   git clone https://github.com/user/Connection-Pool-Library.git
  cd Connection-Pool

2. **Configure the Database**  
   Set up a PostgreSQL database:
   - Create a database
   - Update the credentials in DatabaseConnection.java:
   - Update the database credentials in the `DatabaseConnection.java` file located at:
     `src/com/jakub/bone/database/AirportDatabase.java`
     Replace the placeholders with your database credentials:
     ```java
     private String user = "your_user";
     private String password = "your_password"

3. **Build the Project**   
   Use Gradle to build the project:
   ```bash
   ./gradlew build

## 🛠️ Usage Example

```java
     private String user = "your_user";
     private String password = "your_password"
DatabaseConnection dbConnection = new DatabaseConnection("user", "password", "database", 5432);
ConnectionPool pool = new ConnectionPool(10, 100, dbConnection);

// Acquire a connection
Connection conn = pool.getConnection();

// Execute queries
try (Statement stmt = conn.createStatement()) {
    stmt.executeUpdate("INSERT INTO test_table (data) VALUES ('example');");
}

// Release the connection
pool.releaseConnection(conn);
```

## 🧪 Testing

###The library includes comprehensive tests that validate:

- Performance under query execution
- Stability under high loads
- Proper connection lifecycle management

## 💡 About This Project

This project was developed as an alternative to ready-made solutions, aiming to deepen the understanding
of connection management. It explores the principles of connection pooling, the challenges of resource optimization,
and the intricacies of handling database connections in multi-threaded environments.

## 📧 Contact

If you have any questions, feedback, or suggestions, feel free to reach out to me:

- **Email**: [jakub.bone1990@gmail.com](mailto:jakub.bone1990@gmail,com)
- **Blog**: [javamPokaze.pl](https://javampokaze.pl)  
- **LinkedIn**: [Jakub Bone](https://www.linkedin.com/in/jakub-bone)  

Let's connect and discuss this project further! 🚀
















