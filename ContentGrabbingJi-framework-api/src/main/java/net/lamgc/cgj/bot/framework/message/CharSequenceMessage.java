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

/**
 * 字符序列消息对象.
 * <p> 仅包含了一段字符串.
 * @author LamGC
 */
public final class CharSequenceMessage implements Message {

    private final CharSequence content;

    public CharSequenceMessage(CharSequence content) {
        if (content instanceof Message) {
            this.content = ((Message) content).contentToString();
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
