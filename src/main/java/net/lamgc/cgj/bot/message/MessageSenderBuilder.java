package net.lamgc.cgj.bot.message;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 消息发送器构造
 */
public final class MessageSenderBuilder {

    private final static AtomicReference<MessageSenderFactory> currentFactory = new AtomicReference<>();

    private MessageSenderBuilder() {}

    /**
     * 获取消息发送器
     * @param source 消息源类型
     * @param id 消息源Id
     * @return 返回新建的发送器
     */
    public static MessageSender getMessageSender(MessageSource source, long id) {
        MessageSenderFactory messageSenderFactory = currentFactory.get();
        if(messageSenderFactory == null) {
            throw new IllegalStateException("The factory is not ready");
        }
        try {
            return messageSenderFactory.createMessageSender(source, id);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 设置消息发送器工厂
     * @param factory 工厂对象
     */
    public static void setCurrentMessageSenderFactory(MessageSenderFactory factory) {
        if(currentFactory.get() != null) {
            throw new IllegalStateException("Factory already exists");
        }
        currentFactory.set(factory);
    }

}
