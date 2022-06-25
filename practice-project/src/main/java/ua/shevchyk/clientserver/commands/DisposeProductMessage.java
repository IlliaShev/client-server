package ua.shevchyk.clientserver.commands;

import lombok.*;
import ua.shevchyk.clientserver.utils.Commands;

@Getter
@Setter
@EqualsAndHashCode
public class DisposeProductMessage extends AbstractMessage {

    private String productName;
    private int quantityToDispose;

    public DisposeProductMessage(String group, String productName, int quantityToDispose) {
        super(Commands.DISPOSE_PRODUCT, group);
        this.productName = productName;
        this.quantityToDispose = quantityToDispose;
    }
}
