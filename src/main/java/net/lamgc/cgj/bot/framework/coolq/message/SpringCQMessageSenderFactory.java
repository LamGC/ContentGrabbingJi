package net.lamgc.cgj.bot.framework.coolq.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSenderFactory;
import net.lamgc.cgj.bot.message.MessageSource;
import net.lz1998.cq.robot.CoolQ;

public class SpringCQMessageSenderFactory implements MessageSenderFactory {

    private final CoolQ coolQ;

    public SpringCQMessageSenderFactory(CoolQ coolQ) {
        this.coolQ = coolQ;
    }

    @Override
    public MessageSender createMessageSender(MessageSource source, long id) {
        return new SpringCQMessageSender(coolQ, source, id);
    }
}
