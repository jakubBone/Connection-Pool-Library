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

import database.DatabaseConnection;
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

    public ConnectionPool(int minPoolSize, int maxPoolSize) {
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.pool = new ArrayList<>();
        semaphore = new Semaphore(maxPoolSize, true);
        lock = new ReentrantLock();
        initPool();
    }

    public void initPool()  {
        lock.lock();
        try {
            for (int i = 0; i < minPoolSize; i++) {
                pool.add(new DatabaseConnection().getConnection());
                System.out.println("A new connection added to POOL");
            }
        } catch (SQLException ex) {
            System.out.println("Error initializing connection POOL");
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
                        System.out.println(Thread.currentThread() + " got a connection from the POOL");
                        return conn;
                    }
                }
                if (pool.size() < maxPoolSize) {
                    Connection newConn = new DatabaseConnection().getConnection();
                    pool.add(newConn);
                    System.out.println(Thread.currentThread() + " added a new connection from the POOL");
                    return newConn;
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

    public void releaseConnection(Connection conn) throws SQLException {
        lock.lock();
        try {
            if (!conn.isClosed()) {
                pool.add(conn);
                System.out.println(Thread.currentThread() + " returned the connection to the POOL");
                semaphore.release();
            } else {
                pool.remove(conn);
                System.out.println("Connection with error removed from POOL");
                if (pool.size() < minPoolSize) {
                    pool.add(new DatabaseConnection().getConnection());
                }
            }
        } catch (SQLException ex) {
            pool.remove(conn);
            System.out.println("Connection with error removed from POOL");
            if (pool.size() < minPoolSize) {
                pool.add(new DatabaseConnection().getConnection());
            }
        } finally {
            lock.unlock();
        }
    }

    public void startCleanupScheduler()  {
        System.out.println("Scheduler started");
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                removeIdleConnection();
            } catch (Exception ex) {
                System.out.println("Failed to remove connection: " + ex.getMessage());
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void removeIdleConnection() throws SQLException {
        lock.lock();
        try {
            for (Connection conn: pool) {
                if (conn.isClosed() && pool.size() > minPoolSize) {
                    pool.remove(conn);
                    System.out.println(Thread.currentThread() + " removed the idle connection from POOL");
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void stopCleanupScheduler() throws InterruptedException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException ex) {
                System.out.println("Scheduler shut down");
                scheduler.shutdownNow();
            }
        }
    }
}