package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DatabaseConnection {
    private final String USER = "jakub_bone";
    private final String PASSWORD = "password123";
    private final String DATABASE = "connection_db";
    private final int PORT_NUMBER = 5432;
    private final String URL = String.format("jdbc:postgresql://localhost:%d/%s", PORT_NUMBER, DATABASE);
    private static Connection connection;

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                log.info("Attempting to connect to the database");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                log.info("Connection established with {} on port {}", DATABASE, PORT_NUMBER);
            } catch (SQLException ex) {
                log.error("Error during database connection: {}", ex.getMessage());
                throw ex;
            }
        }
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                log.info("Database disconnected");
            }
        } catch(SQLException ex){
            log.error("Error during database disconnection: {}", ex.getMessage());
        }
    }
}
