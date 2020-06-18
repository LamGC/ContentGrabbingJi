package net.lamgc.cgj.bot.event;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.utils.event.EventObject;

public abstract class MessageEvent implements EventObject, MessageSender {

    private final long fromGroup;
    private final long fromQQ;
    private final String message;

    public MessageEvent(long fromGroup, long fromQQ, String message) {
        this.fromGroup = fromGroup;
        this.fromQQ = fromQQ;
        this.message = message.trim();
    }

    /**
     * 发送消息
     * @param message 消息内容
     * @return 成功返回MessageId, 如没有MessageId则返回0, 失败返回负数错误码
     * @throws Exception 该方法根据不同实现, 可能会抛出不同异常, 详见实现所标识的文档内容.
     */
    @Override
    public abstract int sendMessage(final String message) throws Exception;

    /**
     * 获取图片下载地址.
     * @param image 图片id或图片名
     * @return 下载地址
     */
    public abstract String getImageUrl(String image);

    /**
     * 获取来源群组号
     * @return 如非群组消息, 返回0
     */
    public long getFromGroup() {
        return fromGroup;
    }

    /**
     * 获取消息发送者QQ号
     * @return 消息发送者QQ号
     */
    public long getFromQQ() {
        return fromQQ;
    }

    /**
     * 获取消息内容
     * @return 消息内容;
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.hashCode() + "{" +
                "fromGroup=" + getFromGroup() +
                ", fromQQ=" + getFromQQ() +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
