package net.lamgc.cgj.bot.message;

import net.lz1998.cq.robot.CoolQ;

public class SpringCQMessageSender implements MessageSender {

    private CoolQ coolQ;
    private MessageSource source;
    private long target;

    public SpringCQMessageSender(CoolQ coolQ, MessageSource source, long target) {
        this.coolQ = coolQ;
        this.source = source;
        this.target = target;
    }

    @Override
    public int sendMessage(String message) {
        switch (source) {
            case Private:
                return coolQ.sendPrivateMsg(target, message, false).getData().getMessageId();
            case Group:
                return coolQ.sendGroupMsg(target, message, false).getData().getMessageId();
            case Discuss:
                return coolQ.sendDiscussMsg(target, message, false).getData().getMessageId();
            default:
                return -1;
        }
    }
}
