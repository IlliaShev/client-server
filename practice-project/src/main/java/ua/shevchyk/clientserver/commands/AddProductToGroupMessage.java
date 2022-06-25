package ua.shevchyk.clientserver.commands;

import lombok.*;
import ua.shevchyk.clientserver.utils.Commands;

@Getter
@Setter
@EqualsAndHashCode
public class AddProductToGroupMessage extends AbstractMessage {

    private String productName;

    public AddProductToGroupMessage(String group, String productName) {
        super(Commands.ADD_PRODUCT_TO_GROUP, group);
        this.productName = productName;
    }
}
