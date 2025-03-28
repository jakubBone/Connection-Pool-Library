# 🏗️ Connection-Pool Library
![logo](https://github.com/user-attachments/assets/1a14ee27-2dcd-4644-8484-c922b6d7a878)

The Connection-Pool library is a lightweight and efficient Java solution for managing data connection pools. 
Designed as an alternative to ready-made solutions, this project aims to provide an in-depth understanding of 
connection management and its challenges in multi-threaded environments.

Watch the [Stress Test Video](https://www.youtube.com/watch?v=HYUYge7koJs) to see the implementation in action.


## 🎯 Features

- **Automatic Pool Initialization**: Creates a minimum number of connections at startup

- **Maximum Connection Limit**: Caps the number of concurrent connections to a configured maximum

- **Multithreading Support**: Handles multiple connection requests concurrently

- **Idle Connection Cleanup**: Automatically removes unused connections beyond the minimum limit


## 🚀 Technologies Used

- **Java 21**: Core programming language 

- **JDBC**: Interface for managing data connections

- **JUnit**: Framework for testing

- **Gradle**: Build automation and dependency management


## 📂 Project Structure

```
.
├── src
│   ├── main
│   │   └── java
│   │       ├── connection_pool          # Connection pool management logic
│   │       └── data                     # Database connection handling                               
│   └── test                                       # Unit & performance tests
├── build.gradle                                   # Build configuration
└── ...  
``` 


## 🚀 Getting Started

Follow these steps to set up and run the project:

### Prerequisites

Before you begin, ensure you have the following tools installed:
- **Java Development Kit (JDK)** 21 or higher
- **Gradle** for dependency management
- **PostgreSQL** data

### Setup Instructions

1. **Clone the Repository**  
   Download the project files to your local machine:
   ```bash
   git clone https://github.com/user/Connection-Pool-Library.git
  cd Connection-Pool

2. **Configure the Database**  
   Set up a PostgreSQL data:
   - Create a data
   - Update the credentials in DatabaseConnection.java:
   - Update the data credentials in the `DatabaseConnection.java` file located at:
     `src/com/jakub/bone/data/AirportDatabase.java`
     Replace the placeholders with your data credentials:
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
DatabaseConnection dbConnection = new DatabaseConnection("user", "password", "data", 5432);
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
and the intricacies of handling data connections in multi-threaded environments.

## 📧 Contact

If you have any questions, feedback, or suggestions, feel free to reach out to me:

- **Email**: [jakub.bone1990@gmail.com](mailto:jakub.bone1990@gmail,com)
- **Blog**: [javamPokaze.pl](https://javampokaze.pl)  
- **LinkedIn**: [Jakub Bone](https://www.linkedin.com/in/jakub-bone)  

Let's connect and discuss this project further! 🚀
















