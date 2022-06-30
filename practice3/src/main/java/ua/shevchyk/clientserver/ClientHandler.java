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
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket socketClient;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private Processor processor;

    public ClientHandler(Socket socketClient) {
        this.socketClient = socketClient;
    }

    public ClientHandler(Socket socketClient, Encryptor encryptor, Decryptor decryptor, Processor processor) {
        this.socketClient = socketClient;
        this.encryptor = encryptor;
        this.decryptor = decryptor;
        this.processor = processor;
    }

    @Override
    public void run() {
        try {
            MessageReceiver messageReceiver = new MessageReceiver(socketClient);
            byte[] receivedPacket = messageReceiver.receive();
            Packet packet = decryptor.decrypt(receivedPacket);
            AbstractMessage message = Converter.convertPayloadToMessage(packet.getPacketPayload());
            Message responseMessage = processor.process(message, packet.getPacketPayload().getBUserId());
            byte[] encryptedMessage = encryptor.encrypt(responseMessage, SenderInfo.builder()
                    .cType(packet.getPacketPayload().getCType())
                    .clientId(packet.getPacketPayload().getBUserId())
                    .bSrc(packet.getBSrc()).build());
            messageReceiver.send(encryptedMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
