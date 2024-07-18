import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

        if (pool < MAX_CONNECTION) {
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

    private synchronized void collapseInactiveConnection() throws SQLException{

    // Add 1-minute check interval

        try{
            for (Connection conn: pool) {
                if (conn.isClosed() && pool.size() > 100) {
                    pool.remove(conn);
                }
            }
        } catch (SQLException ex){
            log.error("Failed to check if connection is closed", ex.getMessage());
        }
    }
}