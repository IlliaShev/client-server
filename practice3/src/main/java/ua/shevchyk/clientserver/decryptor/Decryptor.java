package ua.shevchyk.clientserver.decryptor;

import ua.shevchyk.clientserver.models.Packet;

public interface Decryptor {

    Packet decrypt(byte[] message);

}
