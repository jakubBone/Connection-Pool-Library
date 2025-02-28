package performance_test;

import connection_pool.ConnectionPool;
import data.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The ConnectionPoolTest is designed to test the performance and stability of the connection pool.
 * Its primary purpose is to simulate a large number of concurrent database operations using a specified number
 * of connections from the pool.
 *
 * This class demonstrates the ability of the connection pool to handle simultaneous queries and manage
 * connection availability and performance.
 */

class ConnectionPoolPerformanceTest {
    int minPoolSize = 10;
    int maxPoolSize = 100;
    int threadsNumber = 200;
    long testDurationInSeconds = 30;
    ConnectionPool connectionPool;
    DatabaseConnection dbConnection;

    @BeforeEach
    void setUp() {
        dbConnection = new DatabaseConnection("user_manager", "user123", "user_db", 5432);
        connectionPool = new ConnectionPool(minPoolSize, maxPoolSize, dbConnection);
        connectionPool.startCleanupScheduler();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        connectionPool.stopCleanupScheduler();
    }

    @Test
    @DisplayName("Should handle multiple concurrent database operations with connection pool")
    void shouldHandleConcurrentDatabaseOperations() throws InterruptedException {
        // Create task for concurrent performing
        Runnable task = createTask(connectionPool);
        ExecutorService executor = Executors.newFixedThreadPool(threadsNumber);

        // Each thread perform operation on the database
        for (int i = 0; i < threadsNumber; i++) {
            executor.submit(task);
        }

        // Shut down the thread operations in specific time
        executor.shutdown();
        boolean finishedInTime = executor.awaitTermination(testDurationInSeconds + 10, TimeUnit.SECONDS);

        assertTrue(finishedInTime, "Test did not finish in set time");
    }


    // Create task take a connection from the pool, perform operation and return the connection to the pool
    private Runnable createTask(ConnectionPool connectionPool) {
        return () -> {
            long endTime = System.currentTimeMillis() + testDurationInSeconds * 1000;
            String query = "INSERT INTO test_table (IP, STATUS) VALUES ('127.0.0.1', 'active');";
            while (System.currentTimeMillis() < endTime) {
                Connection connection = null;
                try {
                    // Get connection
                    connection = connectionPool.getConnection();

                    // Perform operation
                    System.out.println(Thread.currentThread() + " is performing the operation");
                    updateTable(connection, query);
                } catch (SQLException ex) {
                    System.out.println("Exception: " + ex.getMessage());
                    Thread.currentThread().interrupt();
                } finally {
                    if (connection != null) {
                        try {
                            // Return connection to the pool
                            connectionPool.releaseConnection(connection);
                        } catch (SQLException ex) {
                            System.out.println("Failed to release connection: " + ex.getMessage());
                        }
                    }
                }
            }
        };
    }

    void updateTable(Connection conn, String query){
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}