import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionPoolTester {
    public static void main(String[] args) {
        int minPoolSize = 10;
        int maxPoolSize = 100;
        int numberOfThreads = 200;
        long testDurationInSeconds = 120;

        System.out.println("\n======== TEST STARTED ========\n");

        ConnectionPool connectionPool = new ConnectionPool(minPoolSize, maxPoolSize);
        connectionPool.startCleanupScheduler();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        Runnable task = () -> {
            long endTime = System.currentTimeMillis() + testDurationInSeconds * 1000;
            while (System.currentTimeMillis() < endTime) {
                Connection connection = null;
                try {
                    // CONNECTION GETTING
                    connection = connectionPool.getConnection();

                    // WORK SIMULATION
                    System.out.println(Thread.currentThread() + " is working");
                    Thread.sleep(1000);
                } catch (SQLException | InterruptedException ex) {
                    System.out.println("Exception: " + ex.getMessage());
                    Thread.currentThread().interrupt();
                } finally {
                    if (connection != null) {
                        try {
                            // CONNECTION RELEASING
                            connectionPool.releaseConnection(connection);
                        } catch (SQLException ex) {
                            System.out.println("Failed to release connection: " + ex.getMessage());
                        }
                    }
                }
            }
        };

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(testDurationInSeconds + 10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            System.out.println("Test interrupted: " + ex.getMessage());
            executor.shutdownNow();
        } finally {
            try {
                connectionPool.stopCleanupScheduler();
            } catch (InterruptedException ex) {
                System.out.println("Failed to shutdown connection pool: " + ex.getMessage());
            }
        }
        System.out.println("\n======== TEST FINISHED ========");
    }
}