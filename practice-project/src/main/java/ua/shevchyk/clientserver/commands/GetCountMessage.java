package ua.shevchyk.clientserver.commands;

import lombok.*;
import ua.shevchyk.clientserver.utils.Commands;

@Getter
@Setter
@EqualsAndHashCode
public class GetCountMessage extends AbstractMessage{

    private String productName;

    public GetCountMessage(String group, String productName) {
        super(Commands.GET_COUNT_PRODUCT, group);
        this.productName = productName;
    }
}
