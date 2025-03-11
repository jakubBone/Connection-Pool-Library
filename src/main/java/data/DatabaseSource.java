package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DatabaseSource {
    private final String user;
    private final String password;
    private final String database;
    private final int port;
    private final String URL;
    private Connection connection;

    public DatabaseSource(String user, String password, String database, int port) {
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
        this.URL = String.format("jdbc:postgresql://localhost:%d/%s", port, database);;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                log.info("Attempting to connect to the database '{}' on port {} with user '{}'", database, port, user);
                connection = DriverManager.getConnection(URL, user, password);
                log.info("Connection established successfully with database '{}' on port {}", database, port);
            } catch (SQLException ex) {
                log.error("Failed to establish connection to the database '{}'. Error: {}", database, ex.getMessage(), ex);
                throw ex;
            }
        } else {
            log.info("Reusing existing connection to the database '{}'", database);

        }
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                log.info("Successfully disconnected from the database '{}'", database);
            } else {
                log.warn("Tried to disconnect, but connection was already closed or null for the database '{}'", database);

            }
        } catch(SQLException ex){
            log.error("Error during disconnection from database '{}'. Error: {}", database, ex.getMessage(), ex);
        }
    }
}
