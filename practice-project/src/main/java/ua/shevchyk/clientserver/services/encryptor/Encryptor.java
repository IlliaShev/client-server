package ua.shevchyk.clientserver.services.encryptor;

import ua.shevchyk.clientserver.models.Message;
import ua.shevchyk.clientserver.models.SenderInfo;

public interface Encryptor {

    byte[] encrypt(Message message, SenderInfo info);

}
