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

package net.lamgc.cgj.bot.event;

import net.lamgc.cgj.bot.framework.message.AbstractMessageEvent;

/**
 * 消息事件处理类.
 * <p> 该类将接受原始消息事件, 经消息处理器处理后返回.
 * @author LamGC
 */
public class MessageEventHandler {

    @EventHandler(inheritable = true)
    public void onHandle(AbstractMessageEvent event) {

    }

}
