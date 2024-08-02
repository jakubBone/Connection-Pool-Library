package database;

import database.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseConnectionTest {
    static DatabaseConnection databaseConnection;
    Connection connection;
    @BeforeEach
    void setUp(){
       databaseConnection = new DatabaseConnection();
    }

    @AfterAll
    static void closeDown() {
        databaseConnection.disconnect();
    }

    @Test
    @DisplayName("Should test data base connection return")
    void testGetConnection() throws SQLException {
        connection = databaseConnection.getConnection();

        assertNotNull(connection);
    }
    @Test
    @DisplayName("Should test if is data base connection opened")
    void testIsConnectionOpened() throws SQLException {
        connection = databaseConnection.getConnection();

        assertFalse(connection.isClosed());
    }
}
