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
        long testDurationInSeconds = 60;
        ConnectionPool connectionPool = new ConnectionPool(minPoolSize, maxPoolSize);
        connectionPool.startConnectionRemovalScheduler();

        try {
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            Runnable task = () -> {
                long endTime = System.currentTimeMillis() + testDurationInSeconds * 1000;
                while (System.currentTimeMillis() < endTime) {
                    try {
                        // Connection getting
                        Connection connection = connectionPool.getConnection();

                        // Work simulation
                        Thread.sleep(100);

                        // Connection releasing
                        connectionPool.releaseConnection(connection);

                    } catch (SQLException | InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            };

            for (int i = 0; i < numberOfThreads; i++) {
                executor.submit(task);
            }

            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex){
            System.out.println("Test interrupted: " + ex.getMessage());
        } finally {
            try {
                connectionPool.stopConnectionRemovalScheduler();
            } catch (InterruptedException ex){
                System.out.println("Failed to shutdown connection pool: " + ex.getMessage());
            }
        }
    }
}
