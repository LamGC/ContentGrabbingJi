package net.lamgc.cgj.bot.message;

public interface MessageSender {

    /**
     * 发送消息并返回消息id
     * @param message 消息内容
     * @return 返回非负数则发送成功,
     *          返回0则发送器不支持消息Id,
     *          返回非0正整数则为消息Id,
     *          返回负数则为错误.
     */
    int sendMessage(final String message);

}
