import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ua.clientserver.exceptions.CorruptedPacket;
import ua.clientserver.models.Message;
import ua.clientserver.models.Packet;
import ua.clientserver.services.Decoder;
import ua.clientserver.services.Encoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Practice1Tests {

    private Message message;
    private Encoder encoder;
    private Decoder decoder;

    @BeforeAll
    public void init() {
        encoder = new Encoder();
        decoder = new Decoder();
        message = Message
                .builder()
                .author("Illia Shevchyk")
                .priority(2)
                .text("Text from Illia Shevchyk")
                .build();
    }

    @Test
    public void firstTest() throws Exception {
        byte[] packetBytes = encoder.encode(message);
        Packet receivedPacket = decoder.decode(packetBytes);
        Object receivedObject = decoder.getObjectFromPacket(receivedPacket);
        assertEquals(message, receivedObject);
    }


    @Test
    public void secondTest() throws Exception {
        byte[] packetBytes = encoder.encode(message);
        packetBytes[0] = 0x14;
        CorruptedPacket corruptedPacket = Assertions.assertThrows(CorruptedPacket.class, () -> decoder.decode(packetBytes));
        assertEquals("Incorrect first byte of packet", corruptedPacket.getMessage());
    }

    @Test
    public void thirdTest() throws Exception {
        byte[] packetBytes = encoder.encode(message);
        packetBytes[packetBytes.length - 1] = (byte)(packetBytes[packetBytes.length - 1] - 1);
        CorruptedPacket corruptedPacket = Assertions.assertThrows(CorruptedPacket.class, () -> decoder.decode(packetBytes));
        assertEquals("Crc16 is not matching", corruptedPacket.getMessage());
    }

    @Test
    public void fourthTest() throws Exception {
        byte[] packetBytes = encoder.encode(message);
        packetBytes[1] = (byte)(packetBytes[1] + 1);
        CorruptedPacket corruptedPacket = Assertions.assertThrows(CorruptedPacket.class, () -> decoder.decode(packetBytes));
        assertEquals("Crc16 is not matching", corruptedPacket.getMessage());
    }

}
