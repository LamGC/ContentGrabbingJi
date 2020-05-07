package net.lamgc.cgj.bot.framework.mirai.message;

import net.lamgc.cgj.bot.event.MessageEvent;
import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSource;
import net.mamoe.mirai.message.ContactMessage;
import net.mamoe.mirai.message.GroupMessage;
import net.mamoe.mirai.message.data.MessageUtils;

import java.util.Objects;

public class MiraiMessageEvent extends MessageEvent {

    private final ContactMessage messageObject;
    private final MessageSender messageSender;

    public MiraiMessageEvent(ContactMessage message) {
        super(message instanceof GroupMessage ? ((GroupMessage) message).getGroup().getId() : 0,
                message.getSender().getId(), getMessageBodyWithoutSource(message.getMessage().toString()));
        this.messageObject = Objects.requireNonNull(message);
        if(message instanceof GroupMessage) {
            messageSender = new MiraiMessageSender(((GroupMessage) message).getGroup(), MessageSource.Group);
        } else {
            messageSender = new MiraiMessageSender(message.getSender(), MessageSource.Private);
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
    public int sendMessage(final String message) {
        return messageSender.sendMessage(message);
    }

    @Override
    public String getImageUrl(String imageId) {
        return messageObject.getBot().queryImageUrl(MessageUtils.newImage(imageId));
    }

}
