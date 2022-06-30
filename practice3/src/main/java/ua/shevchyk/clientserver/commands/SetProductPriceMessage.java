package ua.shevchyk.clientserver.commands;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ua.shevchyk.clientserver.utils.Commands;

@Getter
@Setter
@EqualsAndHashCode
public class SetProductPriceMessage extends AbstractMessage {

    private String productName;
    private int price;

    public SetProductPriceMessage(String group, String productName, int price) {
        super(Commands.SET_PRODUCT_PRICE, group);
        this.productName = productName;
        this.price = price;
    }
}
