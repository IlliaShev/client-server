import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ua.shevchyk.clientserver.StoreClientUDP;
import ua.shevchyk.clientserver.StoreServerUDP;
import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.commands.AddProductMessage;
import ua.shevchyk.clientserver.db.DBService;
import ua.shevchyk.clientserver.db.DBServiceImpl;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestUDP {
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
    public void testUDP() throws IOException, InterruptedException {
        int port = 45322;
        AbstractMessage message = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 12);
        AbstractMessage message2 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 5);
        AbstractMessage message3 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 3);
        StoreServerUDP server = new StoreServerUDP(port, dbService);
        StoreClientUDP client = new StoreClientUDP(port, message);
        StoreClientUDP client2 = new StoreClientUDP(port, message2);
        StoreClientUDP client3 = new StoreClientUDP(port, message3);

        new Thread(server).start();

        Thread.sleep(100);


        new Thread(client).start();
        new Thread(client2).start();
        new Thread(client3).start();

        Thread.sleep(1000);
        Assertions.assertEquals(dbService.getCountOfProduct(PRODUCT_NAME), 20);
    }
}
