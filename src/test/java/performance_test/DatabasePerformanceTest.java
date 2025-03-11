package performance_test;

import data.DatabaseSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The DatabasePerformanceTest designed to measure and compare the execution time of
 * executing a simple query multiple times using two different strategies:
 * 1. Establishing and closing a connection for each query
 * 2. Using a single connection to execute all queries
 *
 * This class demonstrates the significant performance impact of repeatedly opening and closing
 * database connections versus reusing a single connection.
 */

class DatabasePerformanceTest {
    String query = "INSERT INTO test_table (IP, STATUS) VALUES ('127.0.0.1', 'active');";
    int operationNumber = 300;

    @Test
    @DisplayName("Should test the performance of executing operations with simple connections")
    void shouldTestWithSimpleConnections() {
        long executionTime = executeTestWithSimpleConnections(query);

        assertTrue(executionTime > 0, "Testing time with simple connections should be > 0");
        System.out.println("Simple connections test duration: " + executionTime + " ms");
    }
    @Test
    @DisplayName("Should test the performance of executing operations with multiple connections")
    void shouldTestWithMultipleConnections() {
        long executionTime = executeTestWithMultipleConnections(query);

        assertTrue(executionTime > 0, "Testing time with multiple connections should be > 0");
        System.out.println("Multiple connections test duration: " + executionTime + " ms");
    }

    // Executes the test with a new connection for each operation
    long executeTestWithSimpleConnections(String query) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < operationNumber; i++) {
            try {
                DatabaseSource dbConnection = new DatabaseSource("user_manager", "user123", "user_db", 5432);
                updateTable(dbConnection.getConnection(), query);
                dbConnection.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        long stop = System.currentTimeMillis();
        return stop - start;
    }

    // Executes the test with a single connection for all operations
    long executeTestWithMultipleConnections(String query)  {
        long start = System.currentTimeMillis();
        DatabaseSource dbConnection = new DatabaseSource("user_manager", "user123", "user_db", 5432);

        for (int i = 0; i < operationNumber; i++) {
             try {
                 updateTable(dbConnection.getConnection(), query);
             } catch (SQLException e){
                 e.printStackTrace();
             }
        }
        dbConnection.disconnect();

        long stop = System.currentTimeMillis();
        return stop - start;
    }

    void updateTable(Connection conn, String query){
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
