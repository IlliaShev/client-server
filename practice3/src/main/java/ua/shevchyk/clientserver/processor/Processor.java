package ua.shevchyk.clientserver.processor;

import ua.shevchyk.clientserver.commands.AbstractMessage;
import ua.shevchyk.clientserver.models.Message;

public interface Processor {

    Message process(AbstractMessage abstractMessage, int id);

}
