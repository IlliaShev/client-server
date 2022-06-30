package ua.shevchyk.clientserver;

import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.commands.AddGroupMessage;
import ua.shevchyk.clientserver.db.DBServiceImpl;

import java.net.UnknownHostException;

public class UPDMain {
    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        int port = 8888;
        DBServiceImpl dbService = new DBServiceImpl();
        AbstractMessage message = new AddGroupMessage("Computers");
        AbstractMessage message2 = new AddGroupMessage("Houses");
        AbstractMessage message3 = new AddGroupMessage("Routers");
        StoreServerUDP server = new StoreServerUDP(port, dbService);
        StoreClientUDP client = new StoreClientUDP(port, message);
        StoreClientUDP client2 = new StoreClientUDP(port, message2);
        StoreClientUDP client3 = new StoreClientUDP(port, message3);

        new Thread(server).start();
        Thread.sleep(100);

        new Thread(client).start();
        new Thread(client2).start();
        new Thread(client3).start();

    }
}
