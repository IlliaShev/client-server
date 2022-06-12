package ua.clientserver.exceptions;

public class CorruptedPacket extends RuntimeException {

    public CorruptedPacket(String message) {
        super(message);
    }

    public CorruptedPacket() {
        this("Exception occurred while converting packet");
    }

}
