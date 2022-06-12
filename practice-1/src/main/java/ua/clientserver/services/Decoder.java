package ua.clientserver.services;

import ua.clientserver.exceptions.CorruptedPacket;
import ua.clientserver.models.Packet;
import ua.clientserver.models.PacketPayload;
import ua.clientserver.utils.CipherFactory;
import ua.clientserver.utils.Crc16;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

import static ua.clientserver.utils.Constants.HEADER_LENGTH;
import static ua.clientserver.utils.Constants.MAGIC_BYTE;
import static ua.clientserver.utils.Functions.getArrayOfBytes;

public class Decoder {

    private final int W_LEN_INDEX = 10;
    private Encoder encoder;

    public Decoder() {
        encoder = new Encoder();
    }

    public Packet decode(byte[] bytes) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        if (byteBuffer.get() != MAGIC_BYTE) {
            throw new CorruptedPacket("Incorrect first byte of packet");
        }

        int wLen = byteBuffer.getInt(W_LEN_INDEX);

        Packet packet = Packet.builder()
                .bSrc(byteBuffer.get())
                .bPktId(byteBuffer.getLong())
                .wLen(byteBuffer.getInt())
                .wCrc16(getwCrc16(byteBuffer))
                .packetPayload(decodePacketPayload(byteBuffer, wLen))
                .wCrc16_end(getwCrc16(byteBuffer))
                .build();

        if (isPacketCorrupted(packet)) {
            throw new CorruptedPacket("Crc16 is not matching");
        }

        return packet;
    }

    private boolean isPacketCorrupted(Packet packet) throws Exception {
        int headerwCrc16 = Crc16.getCrc16(getHeaderBytes(packet));
        int packetwCrc16_end = Crc16.getCrc16(encoder.formPacketPayLoad(packet.getPacketPayload()));
        return packet.getWCrc16() != headerwCrc16 || packet.getWCrc16_end() != packetwCrc16_end;
    }

    private byte[] getHeaderBytes(Packet packet) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(HEADER_LENGTH);
        byteBuffer.put(MAGIC_BYTE)
                .put(packet.getBSrc())
                .putLong(packet.getBPktId())
                .putInt(packet.getWLen());
        byte[] headerBytes = new byte[HEADER_LENGTH];
        byteBuffer.rewind();
        byteBuffer.get(headerBytes);
        return headerBytes;
    }

    private PacketPayload decodePacketPayload(ByteBuffer byteBuffer, int wLen) throws Exception {
        byte[] encryptedBytes = getArrayOfBytes.apply(byteBuffer, wLen);

        ByteBuffer decryptedByteBuffer = ByteBuffer.wrap(CipherFactory.getDecryptCipher().doFinal(encryptedBytes));

        return PacketPayload.builder()
                .cType(decryptedByteBuffer.getInt())
                .bUserId(decryptedByteBuffer.getInt())
                .payload(getArrayOfBytes.apply(decryptedByteBuffer, decryptedByteBuffer.remaining()))
                .build();
    }

    private int getwCrc16(ByteBuffer byteBuffer) {
        return ((byteBuffer.get() & 0xFF) << 0x8) + (byteBuffer.get() & 0xFF);
    }

    public Object getObjectFromPacket(Packet packet) throws Exception {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(packet.getPacketPayload().getPayload());
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }

}
