package ua.shevchyk.clientserver.services.receiver;

import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.models.Packet;
import ua.shevchyk.clientserver.services.decryptor.Decryptor;
import ua.shevchyk.clientserver.services.processor.ProcessorImpl;
import ua.shevchyk.clientserver.utils.RandomPackageCreator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ua.shevchyk.clientserver.utils.Constants.MAX_THREADS;

public class FakeReceiverImpl implements Receiver, Runnable{

    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
    private Decryptor decryptor;
    private Object lock = new Object();

    public byte[] abstractMessage[];
    private int index = 0;

    public FakeReceiverImpl(Decryptor decryptor) {
        this.decryptor = decryptor;
    }

    @Override
    public void receiveMessage() {
        Packet packet;
        synchronized (lock) {
            packet = decryptor.decrypt(abstractMessage[index]);
            index++;
        }
        ProcessorImpl.messageToProcess.add(packet);
    }

    @Override
    public void run() {
        while(true) {
            for (int i = 0; i < abstractMessage.length; i++) {
                executorService.submit(this::receiveMessage);
            }
            break;
            /*try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
        }
    }
}
