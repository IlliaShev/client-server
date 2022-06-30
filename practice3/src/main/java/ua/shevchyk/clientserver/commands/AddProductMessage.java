package ua.shevchyk.clientserver.commands;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ua.shevchyk.clientserver.utils.Commands;

@Getter
@Setter
@EqualsAndHashCode
public class AddProductMessage extends AbstractMessage {

    private String productName;
    private int quantityToAdd;

    public AddProductMessage(String group, String productName, int quantityToAdd) {
        super(Commands.ADD_PRODUCT, group);
        this.productName = productName;
        this.quantityToAdd = quantityToAdd;
    }
}
