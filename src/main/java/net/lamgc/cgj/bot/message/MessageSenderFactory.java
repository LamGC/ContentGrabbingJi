package net.lamgc.cgj.bot.message;

public interface MessageSenderFactory {

    /**
     * 通过Id创建发送器
     * @param source 消息源
     * @param id 消息源id
     * @return 如果成功返回MessageSender
     */
    MessageSender createMessageSender(MessageSource source, long id) throws Exception;

}
