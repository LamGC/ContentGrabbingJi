package net.lamgc.cgj.bot.event;

/**
 * 假负载消息事件, 一般用于预处理某个命令使用，可以增强在高峰期来临时的处理速度.
 */
public class VirtualLoadMessageEvent extends MessageEvent {

    public VirtualLoadMessageEvent(long fromGroup, long fromQQ, String message) {
        super(fromGroup, fromQQ, message);
    }

    @Override
    public int sendMessage(String message) {
        return 0;
    }

    @Override
    public String getImageUrl(String image) {
        return null;
    }

}
