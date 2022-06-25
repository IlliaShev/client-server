package ua.shevchyk.clientserver;

import ua.shevchyk.clientserver.db.DBServiceImpl;
import ua.shevchyk.clientserver.services.decryptor.Decryptor;
import ua.shevchyk.clientserver.services.decryptor.DecryptorImpl;
import ua.shevchyk.clientserver.services.encryptor.Encryptor;
import ua.shevchyk.clientserver.services.encryptor.EncryptorImpl;
import ua.shevchyk.clientserver.services.processor.ProcessorImpl;
import ua.shevchyk.clientserver.services.receiver.FakeReceiverImpl;
import ua.shevchyk.clientserver.services.sender.SenderImpl;

public class Main {
    public static void main(String[] args) {
        Encryptor encryptor = new EncryptorImpl();
        Decryptor decryptor = new DecryptorImpl();
        new Thread(new FakeReceiverImpl(decryptor)).start();
        new Thread(new ProcessorImpl(new DBServiceImpl())).start();
        new Thread(new SenderImpl(encryptor)).start();
    }
}