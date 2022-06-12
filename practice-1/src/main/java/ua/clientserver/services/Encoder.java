package ua.clientserver.services;

import lombok.NoArgsConstructor;
import ua.clientserver.models.PacketPayload;
import ua.clientserver.utils.CipherFactory;
import ua.clientserver.utils.Crc16;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import static ua.clientserver.utils.Constants.MAGIC_BYTE;

@NoArgsConstructor
public class Encoder {

    private static int packetId = 0;
    private byte bSrc = 1;

    public byte[] encode(Object object) throws Exception {
        PacketPayload packetPayload = PacketPayload.builder()
                .cType(1)
                .bUserId(4562)
                .payload(getBytes(object))
                .build();
        byte[] packetPayloadBytes = formPacketPayLoad(packetPayload);

        int wCrc16 = Crc16.getCrc16(packetPayloadBytes);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.BYTES * 18 + packetPayloadBytes.length);
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.put(MAGIC_BYTE)
                .put(bSrc)
                .putLong(packetId++)
                .putInt(packetPayloadBytes.length);
        putwCrc16(byteBuffer, wCrc16);
        byteBuffer.put(packetPayloadBytes);
        putwCrc16(byteBuffer, wCrc16);
        byteBuffer.rewind();
        byteBuffer.get(bytes);
        return bytes;
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
