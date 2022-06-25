package ua.shevchyk.clientserver.services.sender;

import ua.shevchyk.clientserver.models.Message;
import ua.shevchyk.clientserver.models.SenderInfo;
import ua.shevchyk.clientserver.services.encryptor.Encryptor;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ua.shevchyk.clientserver.utils.Constants.MAX_THREADS;

public class SenderImpl implements Sender, Runnable{

    private ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
    public static final BlockingQueue<Map.Entry<Message, SenderInfo>> messageToSend = new ArrayBlockingQueue<>(100);

    private Encryptor encryptor;

    public SenderImpl(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Map.Entry<Message, SenderInfo> message = messageToSend.take();
                System.out.println("Sending packet to " + message.getValue().getClientId());
                executorService.submit(() -> {
                    byte[] packet = encryptor.encrypt(message.getKey(), message.getValue());
                    sendMessage(packet, null);
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendMessage(byte[] mess, InetAddress target) {

    }
}
