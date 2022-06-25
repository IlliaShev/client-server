import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ua.shevchyk.clientserver.commands.*;
import ua.shevchyk.clientserver.db.DBService;
import ua.shevchyk.clientserver.db.DBServiceImpl;
import ua.shevchyk.clientserver.models.SenderInfo;
import ua.shevchyk.clientserver.services.decryptor.Decryptor;
import ua.shevchyk.clientserver.services.decryptor.DecryptorImpl;
import ua.shevchyk.clientserver.services.encryptor.Encryptor;
import ua.shevchyk.clientserver.services.encryptor.EncryptorImpl;
import ua.shevchyk.clientserver.services.processor.ProcessorImpl;
import ua.shevchyk.clientserver.services.receiver.FakeReceiverImpl;
import ua.shevchyk.clientserver.services.sender.Sender;
import ua.shevchyk.clientserver.services.sender.SenderImpl;
import ua.shevchyk.clientserver.utils.Commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Practise2Tests {

    public static final String GROUP_NAME = "Computers";
    public static final String PRODUCT_NAME = "Laptops";
    private DBService dbService;
    private Encryptor encryptor;
    private Decryptor decryptor;

    @BeforeAll
    public void init() {
        dbService = new DBServiceImpl();
        encryptor = new EncryptorImpl();
        decryptor = new DecryptorImpl();
        dbService.addGroup(GROUP_NAME);
        dbService.addProductToGroup(GROUP_NAME, PRODUCT_NAME);
    }

    @Test
    public void test1() throws InterruptedException {
        SenderInfo sender1 = SenderInfo
                .builder()
                .bSrc((byte) 2)
                .clientId(123)
                .cType(Commands.ADD_PRODUCT.ordinal())
                .build();
        SenderInfo sender2 = SenderInfo
                .builder()
                .bSrc((byte) 2)
                .clientId(123)
                .cType(Commands.DISPOSE_PRODUCT.ordinal())
                .build();
        AbstractMessage message1 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 12);
        AbstractMessage message2 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 5);
        AbstractMessage message3 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 3);
        AbstractMessage message4 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 8);
        AbstractMessage message5 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 10);
        AbstractMessage message6 = new AddProductMessage(GROUP_NAME, PRODUCT_NAME, 11);
        AbstractMessage message7 = new DisposeProductMessage(GROUP_NAME, PRODUCT_NAME, 14);

        byte[] bytes[] = new byte[7][];
        bytes[0] = encryptor.encrypt(message1, sender1);
        bytes[1] = encryptor.encrypt(message2, sender1);
        bytes[2] = encryptor.encrypt(message3, sender1);
        bytes[3] = encryptor.encrypt(message4, sender1);
        bytes[4] = encryptor.encrypt(message5, sender1);
        bytes[5] = encryptor.encrypt(message6, sender1);
        bytes[6] = encryptor.encrypt(message7, sender2);

        FakeReceiverImpl fakeReceiver = new FakeReceiverImpl(decryptor);
        fakeReceiver.abstractMessage = bytes;
        Thread receiver = new Thread(fakeReceiver);
        receiver.start();
        new Thread(new ProcessorImpl(dbService)).start();
        new Thread(new SenderImpl(new EncryptorImpl())).start();

        Thread.sleep(100);
        assertEquals(dbService.getCountOfProduct(PRODUCT_NAME), 35);
    }

    @Test
    public void test2() throws InterruptedException {
        SenderInfo sender1 = SenderInfo
                .builder()
                .bSrc((byte) 2)
                .clientId(123)
                .cType(Commands.SET_PRODUCT_PRICE.ordinal())
                .build();
        SenderInfo sender2 = SenderInfo
                .builder()
                .bSrc((byte) 2)
                .clientId(123)
                .cType(Commands.DISPOSE_PRODUCT.ordinal())
                .build();
        AbstractMessage message1 = new SetProductPriceMessage(GROUP_NAME, PRODUCT_NAME, 365);
        AbstractMessage message2 = new DisposeProductMessage(GROUP_NAME, PRODUCT_NAME, 18);

        byte[] bytes[] = new byte[2][];
        bytes[0] = encryptor.encrypt(message1, sender1);
        bytes[1] = encryptor.encrypt(message2, sender2);

        FakeReceiverImpl fakeReceiver = new FakeReceiverImpl(decryptor);
        fakeReceiver.abstractMessage = bytes;
        Thread receiver = new Thread(fakeReceiver);
        receiver.start();
        new Thread(new ProcessorImpl(dbService)).start();
        new Thread(new SenderImpl(new EncryptorImpl())).start();

        Thread.sleep(100);
        assertEquals(dbService.getCountOfProduct(PRODUCT_NAME), 17);
        assertEquals(((DBServiceImpl) dbService).getProduct(PRODUCT_NAME).getPrice(), 365);
    }

}
