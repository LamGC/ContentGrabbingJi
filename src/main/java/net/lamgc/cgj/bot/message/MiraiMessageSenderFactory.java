package net.lamgc.cgj.bot.message;

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
