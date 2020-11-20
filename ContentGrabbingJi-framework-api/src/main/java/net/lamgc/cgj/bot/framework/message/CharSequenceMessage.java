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

import java.util.Objects;

/**
 * 字符序列消息对象.
 * <p> 仅包含了一段字符序列形式的内容.
 * @author LamGC
 */
public final class CharSequenceMessage implements Message {

    private final CharSequence content;

    /**
     * 构造一个 CharSequenceMessage.
     * @param content 字符序列形式的内容.
     * @throws NullPointerException 当 content 为 {@code null},
     *      或 content 为 {@link Message} 且 {@link Message#contentToString()} 返回 {@code null} 时抛出.
     */
    public CharSequenceMessage(CharSequence content) {
        Objects.requireNonNull(content);

        if (content instanceof Message) {
            this.content = Objects.requireNonNull(((Message) content).contentToString());
        } else {
            this.content = content;
        }
    }

    @Override
    public String contentToString() {
        return content.toString();
    }

    @Override
    public String toString() {
        return "CharSequenceMessage{" +
                "content='" + content + '\'' +
                '}';
    }
}
