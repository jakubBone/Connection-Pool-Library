import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class ConnectionPool {
    private final int minPoolSize;
    private final int maxPoolSize;
    private List<Connection> pool;
    private Semaphore semaphore;
    private Lock lock;
    private ScheduledExecutorService scheduler;

    DatabaseConnection databaseConnection;

    public ConnectionPool(int minPoolSize, int maxPoolSize) {
        databaseConnection = new DatabaseConnection();
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.pool = new ArrayList<>();
        semaphore = new Semaphore(200, true);
        lock = new ReentrantLock();
        initPool();
    }

    public void initPool() {
        lock.lock();
        try{
            for (int i = 0; i < minPoolSize; i++) {
                pool.add(new DatabaseConnection().getConnection());
            }
        } finally {
            lock.unlock();
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            semaphore.acquire();
            lock.lock();
            try {
                for (Connection conn : pool) {
                    if (!conn.isClosed()) {
                        pool.remove(conn);
                        log.info("{} get the connection from the POOL", Thread.currentThread());
                        return conn;
                    }
                }
                if (pool.size() < maxPoolSize) {
                    Connection newConnection = new DatabaseConnection().getConnection();
                    pool.add(newConnection);
                    log.info("{} added a new connection to POOL", Thread.currentThread());
                    return newConnection;
                } else {
                    throw new SQLException("No available connections");
                }
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException ex) {
            throw new SQLException("Failed to acquire a connection: {} ", ex.getMessage());
        }
    }

    public void releaseConnection(Connection connection) throws SQLException {
        lock.lock();
        try {
            if (!connection.isClosed()) {
                connection.close();
                pool.add(connection);
                log.info("{} returned the connection to the POOL", Thread.currentThread());
                semaphore.release();
            }
        } catch (SQLException ex) {
            log.error("Failed to check if connection is closed: {}", ex.getMessage());
            connection.close();
        } finally {
            lock.unlock();
        }
    }

    public void removeConnection() throws SQLException {
        lock.lock();
        try{
            for (Connection conn: pool) {
                if (conn.isClosed() && pool.size() > minPoolSize) {
                    pool.remove(conn);
                    log.info("{} removed the useless connection from POOL", Thread.currentThread());
                }
            }
        } catch (SQLException ex){
            log.error("Failed to check if connection is closed: {}", ex.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void startConnectionRemovalScheduler()  {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            try {
                removeConnection();
            } catch (Exception ex) {
                log.error("Failed to remove connection: {}", ex.getMessage());
            }
        }, 1, TimeUnit.MINUTES);
    }

    public void stopConnectionRemovalScheduler() throws InterruptedException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException ex) {
                scheduler.shutdownNow();
            }
        }
    }
}