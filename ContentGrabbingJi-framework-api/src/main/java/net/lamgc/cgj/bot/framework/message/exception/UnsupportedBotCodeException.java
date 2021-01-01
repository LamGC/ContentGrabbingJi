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

package net.lamgc.cgj.bot.framework.message.exception;

/**
 * 不支持的 BotCode 异常.
 * 当 BotCode 的 FunctionName 不受支持时可抛出.
 * @author LamGC
 * @see net.lamgc.cgj.bot.framework.message.BotCode
 * @see net.lamgc.cgj.bot.framework.message.AbstractBotCode
 */
public class UnsupportedBotCodeException extends RuntimeException {

    /**
     * 构造异常
     * @param functionName 功能名
     */
    public UnsupportedBotCodeException(String functionName) {
        super(functionName);
    }
}
