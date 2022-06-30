package ua.shevchyk.clientserver;

import ua.shevchyk.clientserver.db.DBService;
import ua.shevchyk.clientserver.decryptor.DecryptorImpl;
import ua.shevchyk.clientserver.encryptor.EncryptorImpl;
import ua.shevchyk.clientserver.processor.ProcessorImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreServerTCP implements Runnable {

    private int port;
    private ServerSocket server;
    private ExecutorService executorService;
    private DBService dbService;


    public StoreServerTCP(int port, int maxConnections, DBService dbService) throws IOException {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(maxConnections);
        server = new ServerSocket(port);
        this.dbService = dbService;
    }


    @Override
    public void run() {
        try {
            System.out.println("Server is running on port " + port);
            while (true) {
                executorService.execute(new ClientHandler(server.accept(), new EncryptorImpl(), new DecryptorImpl(), new ProcessorImpl(dbService)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
            System.out.println("Server has closed");
        }
    }

    public void shutdown() {
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
