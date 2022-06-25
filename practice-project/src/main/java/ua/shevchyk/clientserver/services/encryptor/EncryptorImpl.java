package ua.shevchyk.clientserver.services.encryptor;



import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.models.Message;
import ua.shevchyk.clientserver.models.Packet;
import ua.shevchyk.clientserver.models.PacketPayload;
import ua.shevchyk.clientserver.models.SenderInfo;
import ua.shevchyk.clientserver.utils.CipherFactory;
import ua.shevchyk.clientserver.utils.Crc16;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import static ua.shevchyk.clientserver.utils.Constants.HEADER_LENGTH;
import static ua.shevchyk.clientserver.utils.Constants.MAGIC_BYTE;


public class EncryptorImpl implements Encryptor {

    private static long packetId = 0;
    private final Object lock = new Object();

    @Override
    public byte[] encrypt(Message message, SenderInfo info) {
        try {
            PacketPayload packetPayload = PacketPayload.builder()
                    .cType(info.getCType())
                    .bUserId(info.getClientId())
                    .payload(getBytes(message))
                    .build();
            byte[] packetPayloadBytes = formPacketPayLoad(packetPayload);


            int wCrc16_end = Crc16.getCrc16(packetPayloadBytes);
            ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.BYTES * 18 + packetPayloadBytes.length);
            byte[] bytes = new byte[byteBuffer.remaining()];
            synchronized (lock) {
                byteBuffer.put(MAGIC_BYTE)
                        .put(info.getBSrc())
                        .putLong(packetId++)
                        .putInt(packetPayloadBytes.length);
            }
            byte[] headerBytes = new byte[HEADER_LENGTH];
            byteBuffer.rewind();
            byteBuffer.get(headerBytes);
            int wCrc16 = Crc16.getCrc16(headerBytes);
            putwCrc16(byteBuffer, wCrc16);
            byteBuffer.put(packetPayloadBytes);
            putwCrc16(byteBuffer, wCrc16_end);
            byteBuffer.rewind();
            byteBuffer.get(bytes);
            return bytes;
        } catch (Exception e) {
            //TODO: add exception
            return null;
        }
    }

    public byte[] formPacketPayLoad(PacketPayload packetPayload) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES * 2 + packetPayload.getPayload().length);
        byte[] payload = new byte[byteBuffer.remaining()];
        byteBuffer.putInt(packetPayload.getCType());
        byteBuffer.putInt(packetPayload.getBUserId());
        byteBuffer.put(packetPayload.getPayload());
        byteBuffer.rewind();
        byteBuffer.get(payload);
        return CipherFactory.getEncryptCipher().doFinal(payload);
    }


    private byte[] getBytes(Object o) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
            return bos.toByteArray();
        }
    }

    private void putwCrc16(ByteBuffer byteBuffer, int wCrc16) {
        byteBuffer.put((byte) (wCrc16 >> 0x8));
        byteBuffer.put((byte) wCrc16);
    }
}
