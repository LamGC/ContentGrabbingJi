package net.lamgc.cgj.bot.event;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSource;
import net.lamgc.cgj.bot.message.MiraiMessageSender;
import net.mamoe.mirai.message.ContactMessage;
import net.mamoe.mirai.message.GroupMessage;
import net.mamoe.mirai.message.data.MessageUtils;

import java.util.Objects;

public class MiraiMessageEvent extends MessageEvent {

    private final ContactMessage messageObject;
    private final MessageSender messageSender;

    public MiraiMessageEvent(ContactMessage message) {
        super(message instanceof GroupMessage ? ((GroupMessage) message).getGroup().getId() : 0,
                message.getSender().getId(), message.getMessage().contentToString());
        this.messageObject = Objects.requireNonNull(message);
        if(message instanceof GroupMessage) {
            messageSender = new MiraiMessageSender(((GroupMessage) message).getGroup(), MessageSource.Group);
        } else {
            messageSender = new MiraiMessageSender(message.getSender(), MessageSource.Private);
        }

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
