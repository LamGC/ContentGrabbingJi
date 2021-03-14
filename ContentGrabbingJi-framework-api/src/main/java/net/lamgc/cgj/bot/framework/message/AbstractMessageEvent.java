/*
 * Copyright (C) 2021  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * ContentGrabbingJi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lamgc.cgj.bot.framework.message;

import net.lamgc.cgj.bot.event.AbstractEventObject;
import net.lamgc.cgj.bot.framework.Platform;

import java.util.Locale;
import java.util.Objects;

/**
 * 消息事件.
 * @author LamGC
 */
public abstract class AbstractMessageEvent extends AbstractEventObject {

    private final Platform fromPlatform;
    private final MessageSource messageSource;
    /**
     * 消息来源 Id.
     * <p> fromId 必须要与 {@link #getSender()} 中的 {@link MessageSender#getId() getId()} 一致.
     */
    private final long fromId;
    /**
     * 消息发送者 Id.
     * <p> 当 Sender 为 Group 等非个人的时候, 将无法获取具体消息发送者, 故添加 SenderId 参数以指向具体发送者.
     * <p> 当 Sender 为消息来源时, SenderId 与 {@link MessageSender#getId()} 一致.
     */
    private final long senderId;
    /**
     * 已被转换为 FAL 形式的消息对象.
     * <p> 注意判断类型, 可能是 {@link MessageChain} 也可能是 {@link CharSequenceMessage} 或者 {@link BotCode}.
     */
    private final Message content;
    private final MessageSender sender;

    /**
     * 语言包.
     * <p> 如果激活 i18n 的话就会使用到该属性, Bot 将使用对应的消息模板构建回复消息内容.
     * <p> 当 Framework 不提供时将使用内置默认语言包.
     */
    private Locale locale = null;

    protected AbstractMessageEvent(
            Platform fromPlatform,
            MessageSource messageSource,
            long fromId,
            long senderId,
            Message messageContent,
            MessageSender sender) {
        this.fromPlatform = Objects.requireNonNull(fromPlatform);
        this.messageSource = Objects.requireNonNull(messageSource);
        this.fromId = fromId;
        this.senderId = senderId;
        this.content = messageContent;
        this.sender = sender;
    }

    public Platform getFromPlatform() {
        return fromPlatform;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public long getFromId() {
        return fromId;
    }

    public long getSenderId() {
        return senderId;
    }

    public Message getContent() {
        return content;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public MessageSender getSender() {
        return sender;
    }

}
