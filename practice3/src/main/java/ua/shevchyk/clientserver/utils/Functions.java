package ua.shevchyk.clientserver.utils;

import java.nio.ByteBuffer;
import java.util.function.BiFunction;

public final class Functions {
    public static final BiFunction<ByteBuffer, Integer, byte[]> getArrayOfBytes = (buffer, length) -> {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    };
}
