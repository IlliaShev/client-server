package ua.shevchyk.clientserver;

import ua.shevchyk.clientserver.db.DBService;
import ua.shevchyk.clientserver.decryptor.DecryptorImpl;
import ua.shevchyk.clientserver.encryptor.EncryptorImpl;
import ua.shevchyk.clientserver.processor.ProcessorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ua.shevchyk.clientserver.utils.Constants.MAX_THREADS;

public class StoreServerUDP implements Runnable {
    private DatagramSocket datagramSocket;
    private ExecutorService executorService;
    private DBService dbService;

    public StoreServerUDP(int port, DBService dbService) {
        this.dbService = dbService;
        executorService = Executors.newFixedThreadPool(MAX_THREADS);
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Running UDP Server");
        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                executorService.shutdown();
                break;
            }
            executorService.execute(new ClientUDPHandler(datagramPacket, new EncryptorImpl(), new DecryptorImpl(), new ProcessorImpl(dbService)));
        }
    }
}
