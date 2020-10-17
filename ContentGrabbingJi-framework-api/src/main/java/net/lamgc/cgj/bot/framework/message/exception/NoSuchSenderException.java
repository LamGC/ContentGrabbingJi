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

package net.lamgc.cgj.bot.framework.message.exception;

/**
 * 无对应 Sender 异常.
 * 当 {@link net.lamgc.cgj.bot.framework.message.MessageSenderFactory} 无法返回对应 Sender 时抛出.
 * @see net.lamgc.cgj.bot.framework.message.MessageSender
 * @see net.lamgc.cgj.bot.framework.message.MessageSenderFactory
 * @author LamGC
 */
public class NoSuchSenderException extends Exception {

    public NoSuchSenderException(String message) {
        super(message);
    }

    public NoSuchSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchSenderException(Throwable cause) {
        super(cause);
    }
}
