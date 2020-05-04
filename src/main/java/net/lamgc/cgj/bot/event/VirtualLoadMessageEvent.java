package net.lamgc.cgj.bot.event;

/**
 * 假负载消息事件, 一般用于预处理某个命令使用，可以增强在高峰期来临时的处理速度.
 */
public class VirtualLoadMessageEvent extends MessageEvent {

    /**
     * 将任意消息事件转换为假负载消息事件.
     * <p>转换之后, 除了fromGroup, fromQQ, message外其他信息不会保留</p>
     * @param event 待转换的消息事件
     * @param inheritImpl 是否继承除 sendMessage 外的其他 MessageEvent 实现
     * @return 转换后的消息事件
     */
    public static VirtualLoadMessageEvent toVirtualLoadMessageEvent(MessageEvent event, boolean inheritImpl) {
        if(event instanceof VirtualLoadMessageEvent) {
            return (VirtualLoadMessageEvent) event;
        } else if(!inheritImpl) {
            return new VirtualLoadMessageEvent(event.getFromGroup(), event.getFromQQ(), event.getMessage());
        } else {
            return new VirtualLoadMessageEvent(event.getFromGroup(), event.getFromQQ(), event.getMessage()) {
                @Override
                public String getImageUrl(String image) {
                    return event.getImageUrl(image);
                }
            };
        }
    }

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
