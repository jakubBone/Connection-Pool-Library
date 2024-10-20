package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DatabaseConnection {
    private String USER;
    private String PASSWORD;
    private String DATABASE;
    private  int PORT_NUMBER;
    private  String URL;
    private Connection connection;
    public DatabaseConnection(String USER, String PASSWORD, String DATABASE, int PORT_NUMBER) {
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        this.DATABASE = DATABASE;
        this.PORT_NUMBER = PORT_NUMBER;
        this.URL = String.format("jdbc:postgresql://localhost:%d/%s", PORT_NUMBER, DATABASE);;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                log.info("Attempting to connect to the database '{}' on port {} with user '{}'", DATABASE, PORT_NUMBER, USER);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                log.info("Connection established successfully with database '{}' on port {}", DATABASE, PORT_NUMBER);
            } catch (SQLException ex) {
                log.error("Failed to establish connection to the database '{}'. Error: {}", DATABASE, ex.getMessage(), ex);
                throw ex;
            }
        } else {
            log.info("Reusing existing connection to the database '{}'", DATABASE);

        }
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                log.info("Successfully disconnected from the database '{}'", DATABASE);
            } else {
                log.warn("Tried to disconnect, but connection was already closed or null for the database '{}'", DATABASE);

            }
        } catch(SQLException ex){
            log.error("Error during disconnection from database '{}'. Error: {}", DATABASE, ex.getMessage(), ex);
        }
    }
}
