package net.lamgc.cgj.bot.framework.mirai.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSenderFactory;
import net.lamgc.cgj.bot.message.MessageSource;
import net.mamoe.mirai.Bot;

import java.util.Objects;

public class MiraiMessageSenderFactory implements MessageSenderFactory {

    private final Bot bot;

    public MiraiMessageSenderFactory(Bot bot) {
        this.bot = bot;
    }

    @Override
    public MessageSender createMessageSender(MessageSource source, long id) {
        Objects.requireNonNull(source);
        if(id <= 0) {
            throw new IllegalArgumentException("id cannot be 0 or negative: " + id);
        }
        return new MiraiMessageSender(bot, source, id);
    }
}
