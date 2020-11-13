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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 事件处理工具类.
 * @author LamGC
 */
public final class EventUtils {

    private EventUtils() {}

    /**
     * 检查 {@link EventExecutor} 是否支持取消事件.
     * @param executor 事件执行器.
     * @return 如果支持, 返回 {@code true}
     */
    public static boolean isSupportedCancel(EventExecutor executor) {
        return executor instanceof SupportedCancel;
    }

    /**
     * 检查方法是否符合事件处理方法条件.
     * @param method 待检查的方法.
     * @return 如果符合, 返回 true.
     */
    public static boolean checkEventHandlerMethod(Method method) {
        int modifiers = method.getModifiers();
        // 新版事件系统将不再允许静态方法作为事件处理方法, 以降低管理难度.
        if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers)) {
            return false;
        }
        if (!method.isAnnotationPresent(EventHandler.class) || method.getParameterCount() != 1) {
            return false;
        }

        Class<?> param = method.getParameterTypes()[0];
        if (!EventObject.class.isAssignableFrom(param)) {
            return false;
        }
        return method.getReturnType().equals(Void.TYPE);
    }

}
