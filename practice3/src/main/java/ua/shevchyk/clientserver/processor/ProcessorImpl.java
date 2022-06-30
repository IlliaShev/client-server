package ua.shevchyk.clientserver.processor;


import ua.shevchyk.clientserver.commands.*;
import ua.shevchyk.clientserver.db.DBService;
import ua.shevchyk.clientserver.models.Message;
import ua.shevchyk.clientserver.models.ResponseMessage;

public class ProcessorImpl implements Processor {
    private final DBService service;

    public ProcessorImpl(DBService dbService) {
        this.service = dbService;
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
