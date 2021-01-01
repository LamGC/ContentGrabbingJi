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
import java.util.Map;

/**
 * 事件处理注册器.
 * <p> 实现了该接口的类将允许注册事件处理方法.
 * @author LamGC
 */
public interface HandlerRegistry {

    /**
     * 注册对象中的事件处理方法.
     * @param handlerObject 包含事件处理方法的对象.\
     * @return 返回已成功添加的方法数.
     */
    int registerHandler(Object handlerObject);

    /**
     * 获取能处理指定事件的所有方法.
     * @param event 待匹配的事件对象.
     * @return 返回一个集合, 集合存储了可处理该事件的所有方法和它所属类的对象.
     */
    Map<Method, Object> getMatchedHandlerMethod(EventObject event);

}
