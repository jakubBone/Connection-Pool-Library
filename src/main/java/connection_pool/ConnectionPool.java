package connection_pool;

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

import data.DatabaseConnection;
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
    private DatabaseConnection dbConnection;

    public ConnectionPool(int minPoolSize, int maxPoolSize, DatabaseConnection dbConnection) {
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.pool = new ArrayList<>();
        this.semaphore = new Semaphore(maxPoolSize, true);
        this.lock = new ReentrantLock();
        this.dbConnection = dbConnection;
        initPool();
    }

    private void initPool()  {
        lock.lock();
        try {
            for (int i = 0; i < minPoolSize; i++) {
                pool.add(dbConnection.getConnection());
                log.info("A new connection added to POOL. Current pool size: {}", pool.size());
            }
        } catch (SQLException ex) {
            log.error("Error initializing connection POOL: {}", ex.getMessage(), ex);
        } finally {
            lock.unlock();
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            semaphore.acquire();
            lock.lock();
            try {
                for (Connection conn: pool) {
                    if (!conn.isClosed()) {
                        pool.remove(conn);
                        log.info("Thread {} got a connection from the POOL. Pool size: {}",
                                Thread.currentThread().getName(), pool.size());
                        return conn;
                    }
                }
                if (pool.size() < maxPoolSize) {
                    Connection newConn = dbConnection.getConnection();
                    if(!pool.contains(newConn)){
                        pool.add(newConn);
                        log.info("Thread {} added a new connection to the POOL. Pool size: {}",
                                Thread.currentThread().getName(), pool.size());
                        return newConn;
                    } else {
                        log.warn("Attempted to add a duplicate connection to the POOL");
                        throw new SQLException("Duplicate connection detected");
                    }
                } else {
                    log.warn("No available connections. Maximum pool size reached");
                    throw new SQLException("No available connections");
                }
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException ex) {
            log.error("Failed to acquire a connection: {}", ex.getMessage(), ex);
            throw new SQLException("Failed to acquire a connection: " + ex.getMessage(), ex);
        }
    }

    public void releaseConnection(Connection conn) throws SQLException {
        lock.lock();
        try {
            if (!conn.isClosed()) {
                pool.add(conn);
                log.info("Thread {} returned the connection to the POOL. Pool size: {}",
                        Thread.currentThread().getName(), pool.size());
                semaphore.release();
            } else {
                pool.remove(conn);
                log.warn("Closed connection detected and removed from POOL. Pool size: {}", pool.size());
                if (pool.size() < minPoolSize) {
                    pool.add(dbConnection.getConnection());
                    log.info("New connection added to POOL to maintain minimum pool size. Pool size: {}", pool.size());
                }
            }
        } catch (SQLException ex) {
            pool.remove(conn);
            log.error("Error occurred while releasing connection: {}", ex.getMessage(), ex);
            if (pool.size() < minPoolSize) {
                pool.add(dbConnection.getConnection());
                log.info("New connection added to POOL after error. Pool size: {}", pool.size());

            }
        } finally {
            lock.unlock();
        }
    }

    public void startCleanupScheduler()  {
        log.info("Scheduler for cleaning idle connections started");
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                removeIdleConnection();
            } catch (Exception ex) {
                log.error("Failed to remove idle connection: {}", ex.getMessage(), ex);

            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void removeIdleConnection() throws SQLException {
        lock.lock();
        try {
            for (Connection conn: pool) {
                if (conn.isClosed() && pool.size() > minPoolSize) {
                    pool.remove(conn);
                    log.info("Removed idle connection from POOL. Pool size: {}", pool.size());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void stopCleanupScheduler() throws InterruptedException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            log.info("Scheduler shutting down");
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    log.warn("Scheduler forced shutdown");
                } else {
                    log.info("Scheduler terminated successfully");
                }
            } catch (InterruptedException ex) {
                log.error("Error shutting down scheduler: {}", ex.getMessage(), ex);
                scheduler.shutdownNow();
            }
        }
    }
}