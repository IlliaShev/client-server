package ua.shevchyk.clientserver.services.sender;

import java.net.InetAddress;

public interface Sender {

    void sendMessage(byte[] mess, InetAddress target);
}
