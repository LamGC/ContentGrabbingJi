package net.lamgc.cgj.bot.event;

import net.lamgc.cgj.bot.message.MessageSender;

public class BufferedMessageSender implements MessageSender {

    private final StringBuffer buffer = new StringBuffer();

    @Override
    public int sendMessage(String message) {
        buffer.append(message);
        return 0;
    }

    /**
     * 从缓冲区中取出消息内容.
     * @return 返回事件发送的消息内容.
     */
    public String getBufferContent() {
        return buffer.toString();
    }

}
