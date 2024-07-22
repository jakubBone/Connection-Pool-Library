import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Setter
public class DatabaseConnection {
    private final String USER = "jakub_bone";
    private final String PASSWORD = "password123";
    private final String DATABASE = "connection_db";
    private final int PORT_NUMBER = 5432;
    private final String URL = String.format("jdbc:postgresql://localhost:%d/%s", PORT_NUMBER, DATABASE);
    private static Connection connection;

    public DatabaseConnection() {
        startConnection();
    }

    public void startConnection() {
        try {
            log.info("Attempting to connect with data base");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if(connection != null){
                log.info("Connection with {} database established on port {}", USER, PORT_NUMBER);
            } else {
                log.info("Failed to connect with {} database established on port {}", USER, PORT_NUMBER);
            }
        } catch (SQLException ex) {
            log.error("Error during database connection: {}", ex.getMessage());
        }
    }

    public Connection getConnection(){
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
