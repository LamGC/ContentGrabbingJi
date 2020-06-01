package net.lamgc.cgj.bot.framework.mirai.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSource;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

import java.util.Objects;

public class MiraiMessageEvent extends net.lamgc.cgj.bot.event.MessageEvent {

    private final MessageEvent messageObject;
    private final MessageSender messageSender;

    /**
     * 通过Mirai的MessageEvent转换成应用支持的MessageEvent.
     * @deprecated 请使用 {@link #covertEventObject(MessageEvent)}方法转换.
     * @param message 消息对象
     * @see #covertEventObject(MessageEvent)
     */
    @Deprecated
    public MiraiMessageEvent(MessageEvent message) {
        super(message instanceof GroupMessageEvent ? ((GroupMessageEvent) message).getGroup().getId() : 0,
                message.getSender().getId(), getMessageBodyWithoutSource(message.getMessage().toString()));
        this.messageObject = Objects.requireNonNull(message);
        if(message instanceof GroupMessageEvent) {
            messageSender = new MiraiMessageSender(((GroupMessageEvent) message).getGroup(), MessageSource.Group);
        } else {
            messageSender = new MiraiMessageSender(message.getSender(), MessageSource.Private);
        }
    }

    /**
     * 通过解析好的信息构造MessageEvent
     * @param messageObject 消息原始对象
     * @param groupId 群组Id, 非群聊或无需使用群聊时, 该参数为0
     * @param qqId 发送者Id, 不能为0
     * @param message 原始消息内容对象, 由构造方法内部解析
     */
    private MiraiMessageEvent(MessageEvent messageObject, long groupId, long qqId, MessageChain message) {
        super(groupId, qqId, getMessageBodyWithoutSource(message.toString()));
        this.messageObject = Objects.requireNonNull(messageObject, "messageObject is null");
        this.messageSender = new MiraiMessageSender(messageObject.getSender(),
                        groupId != 0 ? MessageSource.Group : MessageSource.Private);
    }

    /**
     * 将Mirai原始MessageEvent转换成应用支持的MessageEvent对象
     * @param event 原始消息对象
     * @return 原始消息对象所对应的应用MessageEvent对象.
     * @throws IllegalArgumentException 当出现不支持的Mirai {@link MessageEvent}实现时将抛出异常.
     * @see MessageEvent 原始消息对象
     * @see net.lamgc.cgj.bot.event.MessageEvent 应用消息对象
     */
    public static MiraiMessageEvent covertEventObject(MessageEvent event) throws IllegalArgumentException {
        if(event instanceof GroupMessageEvent) {
            return new MiraiMessageEvent(event,
                    ((GroupMessageEvent) event).getGroup().getId(), event.getSender().getId(), event.getMessage());
        } else if(event instanceof FriendMessageEvent) {
            return new MiraiMessageEvent(event, 0, event.getSender().getId(), event.getMessage());
        } else if(event instanceof TempMessageEvent) {
            return new MiraiMessageEvent(event, 0, event.getSender().getId(), event.getMessage());
        } else {
            throw new IllegalArgumentException("Unsupported event type: " + event.toString());
        }
    }

    /**
     * 将ContactMessage获得的消息内容删除 Mirai:source 并返回.
     * <p>该做法比较保守, 防止Mirai:source位置出现变动.</p>
     * @param message ContactMessage的消息内容;
     * @return 返回删除了Mirai:source的消息
     */
    private static String getMessageBodyWithoutSource(String message) {
        StringBuilder builder = new StringBuilder(message);
        int startIndex = builder.indexOf("[mirai:source:");
        int endIndex = builder.indexOf("]", startIndex) + 1;
        return builder.delete(startIndex, endIndex).toString();
    }

    @Override
    public int sendMessage(final String message) throws Exception {
        return messageSender.sendMessage(message);
    }

    @Override
    public String getImageUrl(String imageId) {
        return messageObject.getBot().queryImageUrl(MessageUtils.newImage(imageId));
    }

}
