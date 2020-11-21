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
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 消息链.
 * <p> 消息链代表了由一组元素组成的消息, 是一个消息内容的整体.
 * @author LamGC
 */
public final class MessageChain implements Message {

    private final List<Message> contents = new CopyOnWriteArrayList<>();

    /**
     * 构造一个无内容消息链对象.
     */
    public MessageChain() {}

    /**
     * 通过一些字符序列构造一个有内容的消息链对象.
     * @param contents 字符序列数组, 用于组成消息链的初始内容.
     */
    public MessageChain(CharSequence... contents) {
        plus(contents);
    }

    /**
     * 添加一组字符序列到消息链尾部.
     * @param contents 消息内容.
     * @throws NullPointerException 当 contents 为 {@code null} 时抛出(元素内含有 {@code null} 不会抛出).
     * @throws IllegalArgumentException 当添加消息链对象本身时抛出.
     */
    public void plus(CharSequence... contents) {
        addMultiContents(contents);
    }

    /**
     * 添加一组 {@link Message} 对象到消息链尾部.
     * @param messages 消息对象.
     * @throws NullPointerException 当 messages 为 {@code null} 时抛出(元素内含有 {@code null} 不会抛出).
     * @throws IllegalArgumentException 当添加消息链对象本身时抛出.
     */
    public void plus(Message... messages) {
        addMultiContents(messages);
    }

    /**
     * 添加一组 {@link BotCode} 对象到消息链尾部.
     * @param botCodes BotCode 对象.
     * @throws NullPointerException 当 botCodes 为 {@code null} 时抛出(元素内含有 {@code null} 不会抛出).
     * @throws IllegalArgumentException 当添加消息链对象本身时抛出.
     */
    public void plus(BotCode... botCodes) {
        addMultiContents(botCodes);
    }

    /**
     * 插入一个字符序列对象到消息链的指定位置中.
     * @param index 插入位置的索引, 该索引为插入元素所在索引.
     *              <p> 比如说 {@code ['a', 'b', 'c']} 插入 [index=2, content='d'],
     *              则插入后为 ['a', 'b', 'd', 'c'], 原本处于 index(2) 的元素会往后移动.
     * @param content 欲插入的消息元素.
     */
    public void insert(int index, CharSequence content) {
        checkMessageEqualThis(Objects.requireNonNull(content));
        contents.add(index, content instanceof Message ? (Message) content : new CharSequenceMessage(content));
    }

    /**
     * 删除指定索引的消息元素.
     * @param index 欲删除的消息元素所在索引.
     * @return 返回被删除的消息元素.
     * @throws IndexOutOfBoundsException 当 index 小于 0 或大于消息链长度时抛出.
     */
    public Message delete(int index) {
        return contents.remove(index);
    }

    /**
     * 清空消息链中的所有元素.
     */
    public void clear() {
        contents.clear();
    }

    /**
     * 检查消息链是否为空.
     * <p> 当消息链内不含任何消息元素时, 消息链为空.
     * <p> 该方法等效于 {@code size() == 0}
     * @see #size()
     * @return 如果消息链不含任何消息元素, 则返回 {@code true}.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * 获取消息链大小.
     * @return 返回消息链中的消息元素数量.
     */
    public int size() {
        return contents.size();
    }

    /**
     * 添加多个内容.
     * @param contents 内容数组.
     * @see #addContent(Message) 底层实现.
     */
    private void addMultiContents(CharSequence[] contents) {
        Objects.requireNonNull(contents);
        for (CharSequence content : contents) {
            if (content == null || content.length() == 0) {
                continue;
            }
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
        checkMessageEqualThis(content);
        contents.add(content);
    }

    /**
     * 检查消息是否为本身.
     * <p> 如果消息为本身, 则抛出异常.
     * @param content 内容对象.
     * @throws IllegalArgumentException 当 content 为 MessageChain 本身时抛出.
     */
    private void checkMessageEqualThis(CharSequence content) {
        if (content == this) {
            throw new IllegalArgumentException("Adding the MessageChain itself is not allowed");
        }
    }

    @Override
    public String contentToString() {
        StringBuilder builder = new StringBuilder();
        for (Message messageElement : contents) {
            builder.append(messageElement.contentToString());
        }
        return builder.toString();
    }

}
