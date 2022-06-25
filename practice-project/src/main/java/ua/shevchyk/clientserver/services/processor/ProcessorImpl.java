package ua.shevchyk.clientserver.services.processor;


import ua.shevchyk.clientserver.commands.*;
import ua.shevchyk.clientserver.db.DBService;
import ua.shevchyk.clientserver.models.Message;
import ua.shevchyk.clientserver.models.Packet;
import ua.shevchyk.clientserver.models.ResponseMessage;
import ua.shevchyk.clientserver.models.SenderInfo;
import ua.shevchyk.clientserver.services.converter.Converter;
import ua.shevchyk.clientserver.services.sender.SenderImpl;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ua.shevchyk.clientserver.utils.Constants.MAX_THREADS;

public class ProcessorImpl implements Processor, Runnable {

    public static final BlockingQueue<Packet> messageToProcess = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<Long> queueOfPacket = new ArrayBlockingQueue<>(1000);
    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

    private final DBService service;

    public ProcessorImpl(DBService dbService) {
        this.service = dbService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Packet packet = messageToProcess.take();
                queueOfPacket.add(packet.getBPktId());
                //            System.out.println("Get message " + packet.getBPktId());
                executorService.submit(() -> {
                    AbstractMessage abstractMessage = Converter.convertPayloadToMessage(packet.getPacketPayload());
                    Message responseMessage = process(abstractMessage, (int) packet.getBPktId());
                    SenderInfo senderInfo = SenderInfo.builder()
                            .bSrc(packet.getBSrc())
                            .cType(packet.getPacketPayload().getCType())
                            .clientId(packet.getPacketPayload().getCType())
                            .build();
                    SenderImpl.messageToSend.add(Map.entry(responseMessage, senderInfo));
                    System.out.println("Send answer to MESSAGE " + packet.getBPktId());
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message process(AbstractMessage abstractMessage, int id) {
        System.out.println("OK " + abstractMessage.getCommand() + " id = " + id);

        try {

            switch (abstractMessage.getCommand()) {
                case GET_COUNT_PRODUCT -> {
                    GetCountMessage message = (GetCountMessage) abstractMessage;
                    service.getCountOfProduct(message.getProductName());
                }
                case DISPOSE_PRODUCT -> {
                    DisposeProductMessage message = (DisposeProductMessage) abstractMessage;
                    service.disposeProduct(message.getProductName(), message.getQuantityToDispose());
                }
                case ADD_PRODUCT -> {
                    AddProductMessage message = (AddProductMessage) abstractMessage;
                    service.addProduct(message.getProductName(), message.getQuantityToAdd());
                }
                case ADD_GROUP -> {
                    AddGroupMessage message = (AddGroupMessage) abstractMessage;
                    service.addGroup(message.getGroup());

                }
                case ADD_PRODUCT_TO_GROUP -> {
                    AddProductToGroupMessage message = (AddProductToGroupMessage) abstractMessage;
                    service.addProductToGroup(message.getGroup(), message.getProductName());
                }
                case SET_PRODUCT_PRICE -> {
                    SetProductPriceMessage message = (SetProductPriceMessage) abstractMessage;
                    service.setPrice(message.getProductName(), message.getPrice());
                }
            }
        } catch (Exception e) {
            return ResponseMessage.builder()
                    .response("Bad request")
                    .build();
        }
        return ResponseMessage.builder()
                .response("OK")
                .build();
    }
}
