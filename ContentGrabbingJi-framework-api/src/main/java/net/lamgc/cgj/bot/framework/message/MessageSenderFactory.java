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

import net.lamgc.cgj.bot.framework.message.exception.NoSuchSenderException;

/**
 * MessageSender 构造工厂接口.
 * 提供给 Core 用于主动获取 Sender 实现部分功能.
 * @author LamGC
 */
public interface MessageSenderFactory {

    /**
     * 取指定消息源的 Sender.
     * @param source 消息源类型
     * @param id 消息源 Id
     * @return 返回对应的 Sender 对象.
     * @throws NoSuchSenderException 当无法获取对应 Sender 时请抛出该异常并附上有关 message 或/和 cause.
     */
    MessageSender getSender(MessageSource source, long id) throws NoSuchSenderException;

}
