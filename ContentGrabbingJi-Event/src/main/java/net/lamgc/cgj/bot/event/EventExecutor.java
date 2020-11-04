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

/**
 * 事件执行器.
 * @author LamGC
 */
public interface EventExecutor {

    /**
     * 执行事件.
     * @param event 事件对象.
     */
    void execute(EventObject event);

    /**
     * 是否为异步事件执行器.
     * @return 如果为异步事件执行器, 返回 {@code true}, 如果为同步事件执行器, 返回 {@code false}.
     */
    boolean isAsync();

}
