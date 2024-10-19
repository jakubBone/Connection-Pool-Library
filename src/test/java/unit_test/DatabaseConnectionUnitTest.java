package unit_test;

import database.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseConnectionUnitTest {
    DatabaseConnection dbConnection;
    Connection connection;
    @BeforeEach
    void setUp(){
        dbConnection = new DatabaseConnection("user_manager", "user123", "user_db", 5432);
    }

    @AfterAll
    void closeDown() {
        dbConnection.disconnect();
    }

    @Test
    @DisplayName("Should test data base connection return")
    void testGetConnection() throws SQLException {
        connection = dbConnection.getConnection();

        assertNotNull(connection);
    }
    @Test
    @DisplayName("Should test if is data base connection opened")
    void testIsConnectionOpened() throws SQLException {
        connection = dbConnection.getConnection();

        assertFalse(connection.isClosed());
    }
}
