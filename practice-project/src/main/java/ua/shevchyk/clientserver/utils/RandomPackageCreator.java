package ua.shevchyk.clientserver.utils;

import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.commands.AddGroupMessage;
import ua.shevchyk.clientserver.models.SenderInfo;
import ua.shevchyk.clientserver.services.encryptor.Encryptor;
import ua.shevchyk.clientserver.services.encryptor.EncryptorImpl;

public class RandomPackageCreator {

    private static final Encryptor encryptor = new EncryptorImpl();

    public static synchronized byte[] getRandomPackage() {
        AbstractMessage message = new AddGroupMessage("Computers");
        SenderInfo senderInfo = SenderInfo.builder()
                .bSrc((byte)(Math.random() * 10))
                .clientId((int) (Math.random() * 1000))
                .cType(message.getCommand().ordinal())
                .build();
        return encryptor.encrypt(message, senderInfo);
    }

}
