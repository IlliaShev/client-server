package ua.shevchyk.clientserver.commands;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ua.shevchyk.clientserver.utils.Commands;

@Getter
@Setter
@EqualsAndHashCode
public class AddGroupMessage extends AbstractMessage {
    public AddGroupMessage(String groupName) {
        super(Commands.ADD_GROUP, groupName);
    }
}
