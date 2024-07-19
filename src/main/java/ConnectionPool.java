import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConnectionPool {
    private final int minPoolSize;
    private final int maxPoolSize;
    private List<Connection> pool;

    public ConnectionPool(int minPoolSize, int maxPoolSize) {
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.pool = new ArrayList<>();
        initPool();
    }

    public void initPool() {
        for (int i = 0; i < minPoolSize; i++) {
            pool.add(new DatabaseConnection().getConnection());
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        try {
            for (Connection conn : pool) {
                if (!conn.isClosed()) {
                    return conn;
                }
            }
        } catch (SQLException ex) {
            log.error("Failed to check if connection is closed: {}", ex.getMessage());
        }

        if (pool.size() < maxPoolSize) {
            Connection newConnection = new DatabaseConnection().getConnection();
            pool.add(newConnection);
            return newConnection;
        } else {
            throw new SQLException("No available connections");
        }
    }

    public synchronized void releaseConnection(Connection connection) throws SQLException {
        try {
            if (!connection.isClosed()) {
                pool.add(connection);
            }
        } catch (SQLException ex) {
            log.error("Failed to check if connection is closed: {}", ex.getMessage());
            connection.close();
        }
    }

    public void removeConnection() throws SQLException {
        try{
            for (Connection conn: pool) {
                if (conn.isClosed() && pool.size() > minPoolSize) {
                    pool.remove(conn);
                }
            }
        } catch (SQLException ex){
            log.error("Failed to check if connection is closed: {}", ex.getMessage());
        }
    }

    public synchronized void scheduleConnectionRemoval() throws Exception {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            try {
                removeConnection();
            } catch (Exception ex) {
                log.error("Failed to remove connection: {}", ex.getMessage());
            }
        }, 1, TimeUnit.MINUTES);
    }
}