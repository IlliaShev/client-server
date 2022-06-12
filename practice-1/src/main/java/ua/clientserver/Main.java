package ua.clientserver;

import ua.clientserver.models.Message;
import ua.clientserver.models.Packet;
import ua.clientserver.services.Decoder;
import ua.clientserver.services.Encoder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws Exception {
        Encoder encoder = new Encoder();
        Decoder decoder = new Decoder();
        Message message = Message
                .builder()
                .author("Illia Shevchyk")
                .priority(2)
                .text("Text from Illia Shevchyk")
                .build();
        Message message2 = Message
                .builder()
                .author("Illia Shevchyk")
                .priority(3)
                .text("Text 2 from Illia Shevchyk")
                .build();
        byte[] packetBytes = encoder.encode(message);
        byte[] packetBytes2 = encoder.encode(message2);

        Packet receivedPacket = decoder.decode(packetBytes2);
        System.out.println(decoder.getObjectFromPacket(receivedPacket));
    }
}