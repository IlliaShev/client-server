import org.junit.jupiter.api.Test;
import ua.clientserver.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void testHello() {
        Message message = new Message();
        message.setMessage("Hello world!");
        assertEquals(message.getMessage(), "Hello world!");
    }

}
