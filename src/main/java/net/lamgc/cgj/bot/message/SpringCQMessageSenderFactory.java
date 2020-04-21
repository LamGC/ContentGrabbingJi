package net.lamgc.cgj.bot.message;

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
