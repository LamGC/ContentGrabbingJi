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
 * BotCode 功能名.
 *
 * <p> 功能码的功能分为两种: 头部功能(HeaderFunction) 和 内容功能(BodyFunction).
 * <p> 头部功能不属于内容的一部分, 它决定了消息的属性, 影响了整条消息的性质(例如消息以匿名形式发送, 语音独占消息等).
 *
 *
 * @see BotCode
 * @author LamGC
 */
public interface BotCodeFunction {

    /**
     * 获取 BotCode 功能名.
     * @return 返回功能名.
     */
    String getFunctionName();

    /**
     * 是否作为头部功能存在.
     *
     * @return 如果该功能属于头部功能的一种, 返回 {@code true}.
     */
    boolean headerFunction();

}
