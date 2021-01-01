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

/**
 * 消息接口.
 * @author LamGC
 */
public interface Message extends CharSequence {

    /**
     * 消息内容到文本.
     * @return 返回内容字符串.
     */
    String contentToString();

    /**
     * 返回该消息经过 {@link #contentToString()} 后的字符串长度.
     * @return 返回 {@link #contentToString()} 返回字符串的长度.
     * @see CharSequence#length()
     */
    @Override
    default int length() {
        return contentToString().length();
    }

    /**
     * 从 {@link #contentToString()} 所返回的字符串中取出指定索引的字符.
     * @param index 字符索引.
     * @return 返回字符串指定索引对应的字符.
     * @throws IndexOutOfBoundsException 如果索引参数为负数或不小于此字符串的长度.
     * @see CharSequence#charAt(int)
     */
    @Override
    default char charAt(int index) {
        return contentToString().charAt(index);
    }

    /**
     * 截取从 {@link #contentToString()} 中返回字符串内的一段字符序列.
     * @param start 截取起始索引.
     * @param end 截取终止索引.
     * @return 返回新的字符序列对象.
     * @throws IndexOutOfBoundsException 如果 beginIndex 或 endIndex 为负,
     *                                   endIndex 大于 {@link #length()}, 或者 beginIndex 大于 endIndex 时抛出.
     * @see CharSequence#subSequence(int, int)
     */
    @Override
    default CharSequence subSequence(int start, int end) {
        return contentToString().subSequence(start, end);
    }
}
