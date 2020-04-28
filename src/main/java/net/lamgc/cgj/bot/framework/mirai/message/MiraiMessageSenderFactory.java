package net.lamgc.cgj.bot.framework.mirai.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSenderFactory;
import net.lamgc.cgj.bot.message.MessageSource;
import net.mamoe.mirai.Bot;

public class MiraiMessageSenderFactory implements MessageSenderFactory {

    private final Bot bot;

    public MiraiMessageSenderFactory(Bot bot) {
        this.bot = bot;
    }

    @Override
    public MessageSender createMessageSender(MessageSource source, long id) throws Exception {
        switch(source) {
            case Group:
            case Discuss:
                return new MiraiMessageSender(bot.getGroup(id), source);
            case Private:
                return new MiraiMessageSender(bot.getFriend(id), source);
            default:
                throw new NoSuchFieldException(source.toString());
        }
    }
}
