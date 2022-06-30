package ua.shevchyk.clientserver;

import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.converter.Converter;
import ua.shevchyk.clientserver.decryptor.Decryptor;
import ua.shevchyk.clientserver.decryptor.DecryptorImpl;
import ua.shevchyk.clientserver.encryptor.Encryptor;
import ua.shevchyk.clientserver.encryptor.EncryptorImpl;
import ua.shevchyk.clientserver.models.Message;
import ua.shevchyk.clientserver.models.Packet;
import ua.shevchyk.clientserver.models.SenderInfo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class StoreClientTCP implements Runnable {
    private int port;
    private int connectionTimeoutUnit = 500;
    private Socket socket;

    private Encryptor encryptor;
    private Decryptor decryptor;
    private AbstractMessage message;

    public StoreClientTCP(int port, AbstractMessage message) {
        this.port = port;
        this.message = message;
        this.encryptor = new EncryptorImpl();
        this.decryptor = new DecryptorImpl();
    }

    private void connect() throws IOException {
        int attempt = 0;

        while (true) {
            try {
                socket = new Socket("localhost", port);
                return;
            } catch (ConnectException e) {
                if (attempt > 3) {
                    System.out.println(Thread.currentThread().getName() + " server is inactive");
                    throw new RuntimeException();
                }

                try {
                    Thread.sleep(connectionTimeoutUnit + connectionTimeoutUnit * attempt);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                ++attempt;
            }
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client " + Thread.currentThread().getId() + ":");
        System.out.println(Thread.currentThread().getName() + " start");

        try {
            int attempt = 0;
            while (true) {

                connect();
                MessageReceiver messageReceiver = new MessageReceiver(socket);

                int clientId = (int) (Math.random() * 100000);
                System.out.println("ClientId " + clientId);
                byte[] messageToSend = encryptor.encrypt(message, SenderInfo.builder()
                        .bSrc((byte) (Math.random() * 10))
                        .clientId(clientId)
                        .cType(message.getCommand().ordinal())
                        .build());
                messageReceiver.send(messageToSend);

                byte[] byteResponse = messageReceiver.receive();
                Packet packet = decryptor.decrypt(byteResponse);
                Message response = (Message) Converter.getObject(packet.getPacketPayload().getPayload());
                System.out.println("Response from server " + response);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + " end");
        }
    }
}
