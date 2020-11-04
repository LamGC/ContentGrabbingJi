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

import java.util.Observer;

/**
 * 可取消接口.
 * <p> 实现了该接口的事件可对其处理进行取消.
 * <p> 取消状态不可撤回.
 * @author LamGC
 */
public interface Cancelable {

    /**
     * 检查事件是否已被取消.
     * @return 如果事件已被取消, 返回 true.
     */
    boolean canceled();

    /**
     * 注册事件取消监听器.
     * <p> 注意: 如果发生取消事件, 无论 {@link java.util.Observable} 是否为事件对象本身, 参数 {@code arg} 都必须传递事件对象自身!
     * @param cancelObserver 观察者对象.
     * @throws UnsupportedOperationException 当该可取消对象不支持 {@link java.util.Observable} 时抛出,
     *         既然不支持观察取消事件, 那么 {@link #observableCancel()} 应当返回 {@code false}, 否则该方法不允许抛出该异常.
     */
    void registerCancelObserver(Observer cancelObserver) throws UnsupportedOperationException;

    /**
     * 是否可观察取消事件.
     * <p> 如果本方法返回 {@code true},
     * 那么 {@link #registerCancelObserver(Observer)} 不允许抛出 {@link UnsupportedOperationException} 异常.
     * @return 如果可以, 返回 true.
     */
    boolean observableCancel();

}
