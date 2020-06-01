package net.lamgc.cgj.bot.framework.coolq.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSenderFactory;
import net.lamgc.cgj.bot.message.MessageSource;
import net.lz1998.cq.robot.CoolQ;

import java.util.Objects;

public class SpringCQMessageSenderFactory implements MessageSenderFactory {

    private final static ThreadLocal<CoolQ> threadCoolQ = new ThreadLocal<>();
    @Override
    public MessageSender createMessageSender(MessageSource source, long id) {
        return new SpringCQMessageSender(
                Objects.requireNonNull(threadCoolQ.get(), "CoolQ object is not included in ThreadLocal"), source, id);
    }
}
