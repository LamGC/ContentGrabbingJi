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
 * 无效 BotCode 异常.
 * @author LamGC
 * @see net.lamgc.cgj.bot.framework.message.BotCode
 * @see net.lamgc.cgj.bot.framework.message.AbstractBotCode
 */
public class InvalidBotCodeException extends Exception {

    public InvalidBotCodeException(String message) {
        super(message);
    }

    public InvalidBotCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBotCodeException(Throwable cause) {
        super(cause);
    }
}
