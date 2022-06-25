package ua.shevchyk.clientserver.services.decryptor;

import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.models.Packet;

public interface Decryptor {

    Packet decrypt(byte[] message);

}
