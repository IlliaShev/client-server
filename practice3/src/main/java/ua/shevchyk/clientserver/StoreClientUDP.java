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
import java.net.*;

public class StoreClientUDP implements Runnable{
    DatagramSocket datagramSocket;
    DatagramPacket datagramPacket;
    InetAddress ip = InetAddress.getLocalHost();
    private final int port;
    private final AbstractMessage abstractMessage;
    private final Encryptor encryptor;
    private final Decryptor decryptor;


    public StoreClientUDP(int port, AbstractMessage abstractMessage) throws UnknownHostException {
        this.port = port;
        this.abstractMessage = abstractMessage;
        this.encryptor = new EncryptorImpl();
        this.decryptor = new DecryptorImpl();

        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean isPacketProcessed = false;
        do {
            int clientId = (int) (Math.random() * 10000);
            System.out.println("Sending to clientId: " + clientId);
            byte[] bytesToSend = encryptor.encrypt(abstractMessage, SenderInfo.builder()
                    .cType(abstractMessage.getCommand().ordinal())
                    .clientId(clientId)
                    .bSrc((byte) (Math.random() * 10))
                    .build());
            datagramPacket = new DatagramPacket(bytesToSend, bytesToSend.length, ip, port);
            try {
                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (true) {
                byte[] buff = new byte[1024];
                DatagramPacket response = new DatagramPacket(buff, buff.length);

                try {
                    datagramSocket.receive(response);
                } catch (IOException e) {
                    datagramSocket.close();
                    System.out.println("Retrying...");
                    break;
                }
                byte[] responseBytes = response.getData();
                Packet packet = decryptor.decrypt(responseBytes);
                if (packet.getPacketPayload().getBUserId() == clientId) {
                    isPacketProcessed = true;
                }
                Message responseMessage = (Message) Converter.getObject(packet.getPacketPayload().getPayload());
                System.out.println("Response: " + responseMessage);
                break;
            }
        }
        while (!isPacketProcessed);
    }
}
