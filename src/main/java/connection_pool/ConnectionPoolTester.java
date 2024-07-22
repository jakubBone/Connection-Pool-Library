package connection_pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionPoolTester {
    private static final int MIN_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 100;
    private static final int NUMBER_OF_THREADS = 200;
    private static final long TEST_DURATION_IN_SECONDS = 120;

    public static void main(String[] args) {
        System.out.println("\n======== TEST START ========\n");

        ConnectionPoolTester test = new ConnectionPoolTester();
        test.runTest();

        System.out.println("\n======== TEST STOP ========");
    }

        private void runTest() {
            ConnectionPool connectionPool = new ConnectionPool(MIN_POOL_SIZE, MAX_POOL_SIZE);
            startCleanup(connectionPool);

            Runnable task = createTask(connectionPool);
            ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                executor.submit(task);
            }

            shutDownExecutor(executor);
            stopCleanup(connectionPool);
        }

        private Runnable createTask(ConnectionPool connectionPool) {
            return () -> {
                long endTime = System.currentTimeMillis() + TEST_DURATION_IN_SECONDS * 1000;
                while (System.currentTimeMillis() < endTime) {
                    Connection connection = null;
                    try {
                        // CONNECTION GETTING
                        connection = connectionPool.getConnection();

                        // WORK SIMULATION
                        System.out.println(Thread.currentThread() + " is working");
                        Thread.sleep(100);
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
        }

        private void shutDownExecutor(ExecutorService executor) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(TEST_DURATION_IN_SECONDS + 10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException ex) {
                System.out.println("Test interrupted: " + ex.getMessage());
                executor.shutdownNow();
            }
        }

        private void startCleanup(ConnectionPool connection) {
            connection.startCleanupScheduler();
        }

        private void stopCleanup(ConnectionPool connection) {
            try {
                connection.stopCleanupScheduler();
            } catch (InterruptedException ex) {
                System.out.println("Failed to shutdown connection pool: " + ex.getMessage());
            }
        }
}