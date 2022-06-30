package ua.shevchyk.clientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class MessageReceiver {

    public static final int MAX_TRIES = 40;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public MessageReceiver(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }


    public byte[] receive() throws IOException {
        byte[] packetBytes = new byte[0];
        int waitingClock = 0;

        while (true) {
            if (inputStream.available() == 0) {
                if(++waitingClock > MAX_TRIES) {
                    return packetBytes;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            byte[] bytesToAdd = inputStream.readNBytes(inputStream.available());
            packetBytes = addBytes(packetBytes, bytesToAdd);
            waitingClock = 0;
            return packetBytes;
        }
    }

    private byte[] addBytes(byte[] arr1, byte[] arr2) {
        byte[] result = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }

    public void send(byte[] msg) throws IOException {
        outputStream.write(msg);
    }

    public void shutdown() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
