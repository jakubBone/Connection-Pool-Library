
import org.junit.jupiter.api.BeforeEach;

class ConnectionPoolTest {
    ConnectionPool connectionPool;

    @BeforeEach
    void setUp(){
        connectionPool = new ConnectionPool(10, 100);
    }
}
