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

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 支持取消接口.
 * <p> 实现了该接口的 {@link EventExecutor} 可对已投递执行的事件进行取消, 以阻止事件被执行.
 * @author LamGC
 */
public interface SupportedCancel {

    /**
     * 通过 EventId 取消事件的处理.
     * @param eventId 事件Id.
     * @return 如果成功, 返回 true, 如果事件已执行完成, 返回 false.
     * @throws UnsupportedOperationException 当事件未实现 {@link Cancelable} 接口时抛出.
     * @throws NoSuchElementException 当 EventId 所属事件在 {@link SupportedCancel} 中无法找到时抛出.
     */
    boolean cancelEvent(UUID eventId) throws UnsupportedOperationException, NoSuchElementException;

    /**
     * 通过 Event 对象取消事件的处理.
     * @param event 事件对象.
     * @return 如果成功, 返回 true, 如果事件已执行完成, 返回 false.
     * @throws UnsupportedOperationException 当事件未实现 {@link Cancelable} 接口时抛出.
     * @throws NoSuchElementException 当事件在 {@link SupportedCancel} 中无法找到时抛出.
     */
    boolean cancelEvent(EventObject event) throws UnsupportedOperationException, NoSuchElementException;

    /**
     * 对可取消对象执行取消处理操作.
     * @param cancelableEvent 可取消对象事件.
     * @return 如果成功, 返回 true, 如果事件已执行完成, 返回 false.
     * @throws NoSuchElementException 当事件在 {@link SupportedCancel} 中无法找到时抛出.
     * @throws IllegalArgumentException 当 Cancelable 对象未实现 {@link EventObject} (即不是一个事件对象) 时抛出.
     */
    boolean cancelEvent(Cancelable cancelableEvent) throws NoSuchElementException, IllegalArgumentException;

}
