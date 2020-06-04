package net.lamgc.cgj.bot.framework.cli;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSenderFactory;
import net.lamgc.cgj.bot.message.MessageSource;

public class ConsoleMessageSenderFactory implements MessageSenderFactory {

    private final static ConsoleMessageSender sender = new ConsoleMessageSender();

    @Override
    public MessageSender createMessageSender(MessageSource source, long id) {
        return sender;
    }
}
