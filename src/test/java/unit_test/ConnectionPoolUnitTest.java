package unit_test;

import connection_pool.ConnectionPool;
import data.DatabaseSource;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionPoolUnitTest {
    ConnectionPool pool;
    int minPoolSize = 10;
    int maxPoolSize = 100;
    DatabaseSource dbConnection;

    @BeforeEach
    void setUp(){
        dbConnection = new DatabaseSource("user_manager", "user123", "user_db", 5432);
        pool = new ConnectionPool(minPoolSize, maxPoolSize, dbConnection);
    }

    @Test
    @DisplayName("Should test if the initial connection pool contains the minimum number of connections")
    void testInitPool(){
        List<Connection> newPool = pool.getPool();

        assertEquals(newPool.size(), pool.getPool().size());
        assertFalse(newPool.size() != 10);
        assertNotNull(newPool);
    }

    @Test
    @DisplayName("Should test connection acquisition using getConnection() from the pool")
    void testGetConnectionAcquisition() throws SQLException {
        Connection conn = null;

        // Getting initial 10 connections from pool
        for (int i = 0; i < 10; i++) {
            conn = pool.getConnection();
        }

        // Ensure that the last acquired connection is not null
        assertNotNull(conn);
    }

    @Test
    @DisplayName("Should test connection removal using getConnection() from the pool")
    void testGetConnectionRemoval() throws SQLException {
        Connection conn = null;

        // Getting initial 10 connections from pool
        for (int i = 0; i < 10; i++) {
            conn = pool.getConnection();
        }

        // Ensure that the acquired connection is removed from the pool
        assertFalse(pool.getPool().contains(conn));
    }

    @Test
    @DisplayName("Should test if connection is returned to the pool")
    void testReleaseConnection() throws SQLException {
        Connection conn = pool.getConnection();

        pool.releaseConnection(conn);

        assertTrue(pool.getPool().contains(conn));
    }
}
