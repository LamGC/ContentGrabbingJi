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
 * 消息来源
 * @author LamGC
 */
public enum MessageSource {
    /**
     * 好友/联系人 私聊消息
     */
    FRIENDS,
    /**
     * 临时私聊事件
     */
    TEMP,
    /**
     * 群组消息
     */
    GROUP,
    /**
     * 讨论组消息
     */
    DISCUSS,
    /**
     * 未知来源.
     * <p> 虽然 MessageSource 提供了这个类型, 但是请不要在实际运行中使用该值.
     * @deprecated 意义不明的不可使用类型.
     */
    @Deprecated
    UNKNOWN
}
