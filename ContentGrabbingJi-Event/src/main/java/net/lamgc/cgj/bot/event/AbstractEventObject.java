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

import java.util.UUID;

/**
 * 抽象事件对象.
 * <p> 已完成对 {@link EventObject#getEventId()} 的实现.
 * @author LamGC
 */
public abstract class AbstractEventObject implements EventObject {

    private final UUID eventId = UUID.randomUUID();

    @Override
    public UUID getEventId() {
        return eventId;
    }

}
