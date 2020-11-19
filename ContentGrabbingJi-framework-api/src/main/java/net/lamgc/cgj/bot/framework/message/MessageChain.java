/*
 * Copyright (C) 2020  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 消息链.
 * <p> 消息链代表了由一组元素组成的消息, 是一个消息内容的整体.
 * @author LamGC
 */
public final class MessageChain implements Message {

    private final List<Message> contents = new CopyOnWriteArrayList<>();

    public MessageChain() {}

    public MessageChain(CharSequence... contents) {
        plus(contents);
    }

    public void plus(CharSequence... content) {
        addMultiContents(content);
    }

    public void plus(Message... message) {
        addMultiContents(message);
    }

    public void plus(BotCode... botCode) {
        addMultiContents(botCode);
    }

    /**
     * 添加多个内容.
     * @param contents 内容数组.
     * @see #addContent(Message) 底层实现.
     */
    private void addMultiContents(CharSequence[] contents) {
        for (CharSequence content : contents) {
            if (content instanceof Message) {
                addContent((Message) content);
            } else {
                addContent(new CharSequenceMessage(content));
            }
        }
    }

    /**
     * 添加消息内容.
     * <p> plus 方法必须且只能通过该方法将内容段添加到 {@link #contents},
     * 因为需要防止 MessageChain 自己添加自己.
     * @param content 消息内容.
     * @throws IllegalArgumentException 不允许 MessageChain 添加本身.
     */
    private void addContent(Message content) {
        if (this.equals(content)) {
            throw new IllegalArgumentException("Adding the MessageChain itself is not allowed");
        }
        contents.add(content);
    }

    @Override
    public String contentToString() {
        StringBuilder builder = new StringBuilder();
        for (Message messageElement : contents) {
            builder.append(messageElement.contentToString());
        }
        return builder.toString();
    }

    @Override
    public int length() {
        return contentToString().length();
    }

    @Override
    public char charAt(int index) {
        return contentToString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return contentToString().subSequence(start, end);
    }

}
