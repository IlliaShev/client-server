package ua.shevchyk.clientserver;


import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.commands.AddGroupMessage;
import ua.shevchyk.clientserver.db.DBServiceImpl;

import java.io.IOException;

import static ua.shevchyk.clientserver.utils.Constants.MAX_THREADS;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 8000;
        DBServiceImpl dbService = new DBServiceImpl();
        AbstractMessage message = new AddGroupMessage("Computers");
        AbstractMessage message2 = new AddGroupMessage("Houses");
        AbstractMessage message3 = new AddGroupMessage("Routers");

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
    }
}