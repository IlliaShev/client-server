import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.commands.AddProductMessage;
import ua.shevchyk.clientserver.db.DBService;
import ua.shevchyk.clientserver.db.DBServiceImpl;
import ua.shevchyk.clientserver.exceptions.CorruptedPacket;
import ua.shevchyk.clientserver.models.Packet;
import ua.shevchyk.clientserver.models.SenderInfo;
import ua.shevchyk.clientserver.services.converter.Converter;
import ua.shevchyk.clientserver.services.decryptor.Decryptor;
import ua.shevchyk.clientserver.services.decryptor.DecryptorImpl;
import ua.shevchyk.clientserver.services.encryptor.Encryptor;
import ua.shevchyk.clientserver.services.encryptor.EncryptorImpl;
import ua.shevchyk.clientserver.utils.Commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Practice1Tests {

    private Encryptor encryptor;
    private Decryptor decryptor;
    private DBService dbService;

    @BeforeAll
    public void init() {
        encryptor = new EncryptorImpl();
        decryptor = new DecryptorImpl();
        dbService = new DBServiceImpl();
    }

    @Test
    public void firstTest() throws Exception {
        AbstractMessage abstractMessage = new AddProductMessage("Computers", "laptop", 20);
        byte[] packetBytes = encryptor.encrypt(abstractMessage, SenderInfo.builder()
                .cType(Commands.ADD_PRODUCT.ordinal())
                .clientId(123)
                .bSrc((byte)(1)).build());
        Packet receivedPacket = decryptor.decrypt(packetBytes);
        AbstractMessage receivedObject = Converter.convertPayloadToMessage(receivedPacket.getPacketPayload());
        assertEquals(abstractMessage, receivedObject);
    }


    @Test
    public void secondTest() throws Exception {
        AbstractMessage abstractMessage = new AddProductMessage("Computers", "laptop", 20);
        byte[] packetBytes = encryptor.encrypt(abstractMessage, SenderInfo.builder()
                .cType(Commands.ADD_PRODUCT.ordinal())
                .clientId(123)
                .bSrc((byte)(1)).build());
        packetBytes[0] = 0x14;
        CorruptedPacket corruptedPacket = Assertions.assertThrows(CorruptedPacket.class, () -> decryptor.decrypt(packetBytes));
        assertEquals("Incorrect first byte of packet", corruptedPacket.getMessage());
    }

    @Test
    public void thirdTest() throws Exception {
        AbstractMessage abstractMessage = new AddProductMessage("Computers", "laptop", 20);
        byte[] packetBytes = encryptor.encrypt(abstractMessage, SenderInfo.builder()
                .cType(Commands.ADD_PRODUCT.ordinal())
                .clientId(123)
                .bSrc((byte)(1)).build());
        packetBytes[packetBytes.length - 1] = (byte)(packetBytes[packetBytes.length - 1] - 1);
        CorruptedPacket corruptedPacket = Assertions.assertThrows(CorruptedPacket.class, () -> decryptor.decrypt(packetBytes));
        assertEquals("Crc16 is not matching", corruptedPacket.getMessage());
    }

    @Test
    public void fourthTest() throws Exception {
        AbstractMessage abstractMessage = new AddProductMessage("Computers", "laptop", 20);
        byte[] packetBytes = encryptor.encrypt(abstractMessage, SenderInfo.builder()
                .cType(Commands.ADD_PRODUCT.ordinal())
                .clientId(123)
                .bSrc((byte)(1)).build());
        packetBytes[1] = (byte)(packetBytes[1] + 1);
        CorruptedPacket corruptedPacket = Assertions.assertThrows(CorruptedPacket.class, () -> decryptor.decrypt(packetBytes));
        assertEquals("Crc16 is not matching", corruptedPacket.getMessage());
    }

}