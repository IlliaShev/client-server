package ua.shevchyk.clientserver.services.decryptor;


import ua.shevchyk.clientserver.exceptions.CorruptedPacket;
import ua.shevchyk.clientserver.models.Packet;
import ua.shevchyk.clientserver.models.PacketPayload;
import ua.shevchyk.clientserver.utils.CipherFactory;
import ua.shevchyk.clientserver.utils.Crc16;

import java.nio.ByteBuffer;

import static ua.shevchyk.clientserver.utils.Constants.HEADER_LENGTH;
import static ua.shevchyk.clientserver.utils.Constants.MAGIC_BYTE;
import static ua.shevchyk.clientserver.utils.Functions.getArrayOfBytes;

public class DecryptorImpl implements Decryptor{
    @Override
    public Packet decrypt(byte[] message) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(message);

        if (byteBuffer.get() != MAGIC_BYTE) {
            throw new CorruptedPacket("Incorrect first byte of packet");
        }

        Packet packet = Packet.builder()
                .bSrc(byteBuffer.get())
                .bPktId(byteBuffer.getLong())
                .wLen(byteBuffer.getInt())
                .wCrc16(getwCrc16(byteBuffer))
                .build();

        byte[] messagePayload = getArrayOfBytes.apply(byteBuffer, packet.getWLen());
        packet.setWCrc16_end(getwCrc16(byteBuffer));

        if (isPacketCorrupted(packet, messagePayload)) {
            throw new CorruptedPacket("Crc16 is not matching");
        }

        try {
            packet.setPacketPayload(decodePacketPayload(messagePayload));
        } catch (Exception e) {
            //TODO: logging
            return null;
        }

        return packet;
    }

    private PacketPayload decodePacketPayload(byte[] messagePayload) throws Exception {

        ByteBuffer decryptedByteBuffer = ByteBuffer.wrap(CipherFactory.getDecryptCipher().doFinal(messagePayload));

        return PacketPayload.builder()
                .cType(decryptedByteBuffer.getInt())
                .bUserId(decryptedByteBuffer.getInt())
                .payload(getArrayOfBytes.apply(decryptedByteBuffer, decryptedByteBuffer.remaining()))
                .build();
    }

    private boolean isPacketCorrupted(Packet packet, byte[] messagePayload) {
        int headerwCrc16 = Crc16.getCrc16(getHeaderBytes(packet));
        int packetwCrc16_end = Crc16.getCrc16(messagePayload);
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

    private int getwCrc16(ByteBuffer byteBuffer) {
        return ((byteBuffer.get() & 0xFF) << 0x8) + (byteBuffer.get() & 0xFF);
    }
}
