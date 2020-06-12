package net.lamgc.cgj.bot.framework.coolq.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSource;
import net.lz1998.cq.robot.CoolQ;

public class SpringCQMessageSender implements MessageSender {

    private final CoolQ coolQ;
    private final MessageSource source;
    private final long target;

    public SpringCQMessageSender(CoolQ coolQ, MessageSource source, long target) {
        this.coolQ = coolQ;
        this.source = source;
        this.target = target;
    }

    @Override
    public int sendMessage(String message) {
        switch (source) {
            case PRIVATE:
                return coolQ.sendPrivateMsg(target, message, false).getData().getMessageId();
            case GROUP:
                return coolQ.sendGroupMsg(target, message, false).getData().getMessageId();
            case DISCUSS:
                return coolQ.sendDiscussMsg(target, message, false).getData().getMessageId();
            default:
                return -1;
        }
    }
}
