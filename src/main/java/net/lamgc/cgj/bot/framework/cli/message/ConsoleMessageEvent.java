package net.lamgc.cgj.bot.framework.cli.message;

import net.lamgc.cgj.bot.event.MessageEvent;
import net.lamgc.cgj.bot.message.MessageSenderBuilder;
import net.lamgc.cgj.bot.message.MessageSource;

public class ConsoleMessageEvent extends MessageEvent {

    public ConsoleMessageEvent(long groupId, long qqId, String message) {
        super(groupId, qqId, message);
    }

    @Override
    public int sendMessage(String message) throws Exception {
        if(getFromGroup() <= 0) {
            return MessageSenderBuilder
                    .getMessageSender(MessageSource.PRIVATE, getFromQQ()).sendMessage(message);
        } else {
            return MessageSenderBuilder
                    .getMessageSender(MessageSource.GROUP, getFromQQ()).sendMessage(message);
        }

    }

    @Override
    public String getImageUrl(String image) {
        return null;
    }
}
