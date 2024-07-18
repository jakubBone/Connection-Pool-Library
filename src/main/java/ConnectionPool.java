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

    private final int MIN_CONNECTION = 10;
    private final int MAX_CONNECTION = 100;
    private List<Connection> pool;

    public ConnectionPool() {
        this.pool = new ArrayList<>();
        createPool();
    }

    private void createPool() {
        for (int i = 0; i < MIN_CONNECTION; i++) {
            pool.add(new DatabaseConnection().getConnection())
        }
    }

    private synchronized Connection getActiveConnection() throws SQLException {
        try {
            for (Connection conn : pool) {
                if (!conn.isClosed()) {
                    return conn;
                }
            }
        } catch (SQLException ex) {
            log.error("Failed to check if connection is closed", ex.getMessage());
        }

        if (pool.size() < MAX_CONNECTION) {
            Connection newConnection = new DatabaseConnection();
            pool.add(newConnection);
            return newConnection;
        } else {
            throw new SQLException("No available connections");
        }
    }

    private synchronized void returnConnectionToPool(Connection connection) throws SQLException {
        try {
            if (!connection.isClosed()) {
                pool.add(connection);
            }
        } catch (SQLException ex) {
            log.error("Failed to check if connection is closed", ex.getMessage());
            connection.close();
        }
    }

    private void collapseInactiveConnection() throws SQLException{
        try{
            for (Connection conn: pool) {
                if (conn.isClosed() && pool.size() > MAX_CONNECTION) {
                    pool.remove(conn);
                }
            }
        } catch (SQLException ex){
            log.error("Failed to check if connection is closed", ex.getMessage());
        }
    }

    private synchronized void scheduleConnectionCollapse() throws Exception{
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            try {
                collapseInactiveConnection();
            } catch (Exception ex) {
                log.error("Failed to collapse inactive connections", ex.getMessage());
            }
        }, 1, TimeUnit.MINUTES);
    }
}