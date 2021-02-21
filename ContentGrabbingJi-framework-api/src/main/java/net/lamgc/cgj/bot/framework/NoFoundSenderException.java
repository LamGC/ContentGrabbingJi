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

package net.lamgc.cgj.bot.framework;

import net.lamgc.cgj.bot.framework.message.MessageSource;

/**
 * 无对应消息源异常.
 * <p> 当 {@link SenderFactory} 无法通过传入的 {@link MessageSource} 和 Id 找到对应消息源时, 将抛出本异常.
 * @see SenderFactory
 */
public class NoFoundSenderException extends RuntimeException {

    private final MessageSource source;
    private final long id;

    /**
     * 构造异常.
     * @param source 传入的消息源类型.
     * @param id 传入的消息源 Id.
     */
    public NoFoundSenderException(MessageSource source, long id) {
        super("Source Type: " + source + ", id: " + id);
        this.source = source;
        this.id = id;
    }

    /**
     * 获取引发该异常时传入的 {@link MessageSource} 类型.
     * @return 返回引发该异常时提供的 MessageSource.
     */
    public MessageSource getSource() {
        return source;
    }

    /**
     * 获取引发该异常时传入的消息源 Id.
     * @return 返回引发该异常时提供的消息源 Id.
     */
    public long getId() {
        return id;
    }
}
