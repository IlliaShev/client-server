import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ua.shevchyk.clientserver.StoreClientTCP;
import ua.shevchyk.clientserver.StoreServerTCP;
import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.commands.AddProductMessage;
import ua.shevchyk.clientserver.db.DBService;
import ua.shevchyk.clientserver.db.DBServiceImpl;

import java.io.IOException;

import static ua.shevchyk.clientserver.utils.Constants.MAX_THREADS;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTCP {
    public static final String GROUP_NAME = "Computers";
    public static final String PRODUCT_NAME = "Laptops";
    private DBService dbService;

    @BeforeAll
    public void init() {
        dbService = new DBServiceImpl();
        dbService.addGroup(GROUP_NAME);
        dbService.addProductToGroup(GROUP_NAME, PRODUCT_NAME);
    }

    @Test()
    public void testTCP() throws IOException, InterruptedException {
        int port = 45322;
        AbstractMessage message = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 12);
        AbstractMessage message2 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 5);
        AbstractMessage message3 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 3);
        StoreServerTCP server = new StoreServerTCP(port, MAX_THREADS, dbService);
        StoreClientTCP client = new StoreClientTCP(port, message);
        StoreClientTCP client2 = new StoreClientTCP(port, message2);
        StoreClientTCP client3 = new StoreClientTCP(port, message3);

        new Thread(server).start();

        Thread.sleep(100);


        new Thread(client).start();
        new Thread(client2).start();
        new Thread(client3).start();

        Thread.sleep(1000);
        server.shutdown();
        Assertions.assertEquals(dbService.getCountOfProduct(PRODUCT_NAME), 20);
    }
}
