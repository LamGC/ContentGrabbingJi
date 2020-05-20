package net.lamgc.cgj.bot.event;

import java.util.Objects;

public class BufferMessageEvent extends MessageEvent {

    StringBuffer buffer = new StringBuffer();

    public final MessageEvent parent;

    /**
     * 以空消息空Id生成BufferMessageEvent
     */
    public BufferMessageEvent() {
        super(0, 0, "");
        parent = null;
    }

    /**
     * 提供消息内容构造BufferMessageEvent
     * @param message 传入的消息内容
     */
    public BufferMessageEvent(String message) {
        super(0, 0, message);
        parent = null;
    }

    /**
     * 使用事件构造BufferMessageEvent
     * @param parentEvent 父级消息事件对象
     */
    public BufferMessageEvent(MessageEvent parentEvent) {
        super(parentEvent.getFromGroup(), parentEvent.getFromQQ(), parentEvent.getMessage());
        parent = parentEvent;
    }

    @Override
    public int sendMessage(String message) {
        buffer.append(message);
        return 0;
    }

    /**
     * 当提供了父级消息事件时, 本方法调用父级消息事件对象的{@code getImageUrl(String)}, 如果没有, 返回{@code null}
     */
    @Override
    public String getImageUrl(String image) {
        return Objects.isNull(this.parent) ? null : this.parent.getImageUrl(image);
    }

    /**
     * 获取缓冲区消息内容
     * @return 消息内容
     */
    public String getBufferMessage() {
        return buffer.toString();
    }

}