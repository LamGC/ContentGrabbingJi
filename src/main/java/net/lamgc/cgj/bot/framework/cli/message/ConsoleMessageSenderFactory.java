package net.lamgc.cgj.bot.framework.cli.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSenderFactory;
import net.lamgc.cgj.bot.message.MessageSource;

public class ConsoleMessageSenderFactory implements MessageSenderFactory {

    @Override
    public MessageSender createMessageSender(MessageSource source, long id) {
        return new ConsoleMessageSender(source, id);
    }
}
