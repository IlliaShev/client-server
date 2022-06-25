package ua.shevchyk.clientserver.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.shevchyk.clientserver.models.Message;
import ua.shevchyk.clientserver.utils.Commands;

import java.io.Serializable;

@Data
public abstract class AbstractMessage implements Serializable, Message {

    protected transient Commands command;
    protected String group;

    public AbstractMessage(Commands command, String group) {
        this.command = command;
        this.group = group;
    }
}
