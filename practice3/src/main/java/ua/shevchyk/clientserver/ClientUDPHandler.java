package ua.shevchyk.clientserver;

import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.converter.Converter;
import ua.shevchyk.clientserver.decryptor.Decryptor;
import ua.shevchyk.clientserver.encryptor.Encryptor;
import ua.shevchyk.clientserver.models.Message;
import ua.shevchyk.clientserver.models.Packet;
import ua.shevchyk.clientserver.models.SenderInfo;
import ua.shevchyk.clientserver.processor.Processor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientUDPHandler implements Runnable {

    private DatagramSocket ds;
    private DatagramPacket datagramPacket;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private Processor processor;

    public ClientUDPHandler(DatagramPacket datagramPacket, Encryptor encryptor, Decryptor decryptor, Processor processor) {
        this.datagramPacket = datagramPacket;
        this.encryptor = encryptor;
        this.decryptor = decryptor;
        this.processor = processor;
    }

    @Override
    public void run() {
        System.out.println("Processing packet");
        byte[] bytesToProcess = datagramPacket.getData();
        Packet packet = decryptor.decrypt(bytesToProcess);
        AbstractMessage abstractMessage = Converter.convertPayloadToMessage(packet.getPacketPayload());
        Message responseMessage = processor.process(abstractMessage, packet.getPacketPayload().getBUserId());
        byte[] responseBytes = encryptor.encrypt(responseMessage, SenderInfo.builder()
                .bSrc(packet.getBSrc())
                .clientId(packet.getPacketPayload().getBUserId())
                .cType(packet.getPacketPayload().getCType())
                .build());
        try {
            ds = new DatagramSocket();
            DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length, datagramPacket.getAddress(), datagramPacket.getPort());
            ds.send(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ds.close();
    }
}
