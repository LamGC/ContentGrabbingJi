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

import net.lamgc.cgj.bot.framework.message.BotCode;

/**
 * 构造 BotCode 异常.
 * 当出现某些原因导致无法继续构造 BotCode 时可抛出.
 * 需要说明原因.
 * @author LamGC
 */
public class BuildBotCodeException extends Exception {

    public BuildBotCodeException(String message) {
        super(message);
    }

    public BuildBotCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuildBotCodeException(Throwable cause) {
        super(cause);
    }
}
