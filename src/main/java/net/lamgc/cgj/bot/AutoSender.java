package net.lamgc.cgj.bot;

import java.util.Objects;

/**
 * 自动发送器
 */
public abstract class AutoSender {

    private MessageSender messageSender;

    /**
     * 构造一个自动发送器
     * @param messageSender 自动发送器所使用的消息发送器
     */
    public AutoSender(MessageSender messageSender) {
        this.messageSender = Objects.requireNonNull(messageSender);
    }

    /**
     * 获取设置等等消息发送器
     * @return 消息发送器
     */
    MessageSender getMessageSender() {
        return this.messageSender;
    }

    public abstract void send();

}
