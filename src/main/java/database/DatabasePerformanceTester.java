package database;

import java.sql.*;

/**
 * The DatabasePerformanceTester class is designed to measure and compare the execution time of
 * running a simple query multiple times using two different strategies:
 * 1. Establishing and closing a connection for each query.
 * 2. Using a single connection to execute all queries.
 *
 * This class demonstrates the significant performance impact of repeatedly opening and closing
 * database connections versus reusing a single connection.
 */

public class DatabasePerformanceTester {
    private void executeTestWithSimpleConnections(String query) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 300; i++) {
            try {
                DatabaseConnection conn = new DatabaseConnection();
                updateTable(conn.getConnection(), query);
                conn.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        long stop = System.currentTimeMillis();

        long testTime = (stop - start) ;

        System.out.println("\n SimpleConnectionsTest in millis " + testTime);
    }

    private void executeTestWithMultipleConnections(String query)  {
        long start = System.currentTimeMillis();
        DatabaseConnection conn = new DatabaseConnection();

        for (int i = 0; i < 300; i++) {
             try {
                 updateTable(conn.getConnection(), query);
             } catch (SQLException e){
                 e.printStackTrace();
             }
        }
        conn.disconnect();

        long stop = System.currentTimeMillis();
        long testTime = (stop - start);

        System.out.println("MultipleConnectionsTest in millis: " + testTime + "\n");
    }

    public void updateTable(Connection conn, String query){
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException{
        DatabasePerformanceTester tester = new DatabasePerformanceTester();
        String query = "INSERT INTO test_table (IP, STATUS) VALUES ('127.0.0.1', 'active');";
        tester.executeTestWithSimpleConnections(query);
        tester.executeTestWithMultipleConnections(query);

    }
}
