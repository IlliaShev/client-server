package ua.shevchyk.clientserver.converter;



import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.models.PacketPayload;
import ua.shevchyk.clientserver.utils.Commands;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class Converter {

    public static synchronized AbstractMessage convertPayloadToMessage(PacketPayload packetPayload) {
        return switch (packetPayload.getCType()) {
            case 0 -> getCommand(packetPayload.getPayload(), Commands.GET_COUNT_PRODUCT);
            case 1 -> getCommand(packetPayload.getPayload(), Commands.DISPOSE_PRODUCT);
            case 2 -> getCommand(packetPayload.getPayload(), Commands.ADD_PRODUCT);
            case 3 -> getCommand(packetPayload.getPayload(), Commands.ADD_GROUP);
            case 4 -> getCommand(packetPayload.getPayload(), Commands.ADD_PRODUCT_TO_GROUP);
            case 5 -> getCommand(packetPayload.getPayload(), Commands.SET_PRODUCT_PRICE);
            default -> null;
        };
    }


    private static AbstractMessage getCommand(byte[] commandBytes, Commands command) {
        AbstractMessage message =
                (AbstractMessage) getObject(commandBytes);
        message.setCommand(command);
        return message;
    }

    public static Object getObject(byte[] bytesToDecrypt) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytesToDecrypt);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

}
