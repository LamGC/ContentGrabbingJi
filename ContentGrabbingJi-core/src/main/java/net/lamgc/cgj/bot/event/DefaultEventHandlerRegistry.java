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
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 默认的事件处理注册器.
 * @author LamGC
 */
public class DefaultEventHandlerRegistry implements EventHandlerRegistry {

    private final Map<Method, Object> instanceMap = new Hashtable<>();
    private final Map<Class<? extends EventObject>, Set<Method>> eventHandlerMap = new Hashtable<>();

    @Override
    public int registerHandler(Object handlerObject) {
        int count = 0;
        Class<?> handlerClass = handlerObject.getClass();
        Method[] methods = handlerClass.getDeclaredMethods();
        for (Method method : methods) {
            if (!EventUtils.checkEventHandlerMethod(method)) {
                continue;
            }
            addHandlerMethod(method, handlerObject);
            count++;
        }

        return count;
    }

    private void addHandlerMethod(Method method, Object instance) {
        instanceMap.put(method, instance);
        Class<? extends EventObject> eventObjectClass = getParameterTypeClass(method);
        if (!eventHandlerMap.containsKey(eventObjectClass)) {
            eventHandlerMap.put(eventObjectClass, new CopyOnWriteArraySet<>());
        }
        eventHandlerMap.get(eventObjectClass).add(method);
    }

    private Class<? extends EventObject> getParameterTypeClass(Method method) {
        Class<?> parameterType = method.getParameterTypes()[0];
        if (EventObject.class.isAssignableFrom(parameterType)) {
            throw new IllegalArgumentException("Wrong parameter type: " + parameterType.getName());
        }
        return parameterType.asSubclass(EventObject.class);
    }

    @Override
    public Map<Method, Object> getMatchedHandlerMethod(EventObject event) {
        Map<Method, Object> result = new HashMap<>(0);
        Set<Method> methods = findHandleMethod(event.getClass());
        for (Method method : methods) {
            result.put(method, instanceMap.get(method));
        }
        return result;
    }

    /**
     * 查找可处理的方法.
     * @param eventClass 事件 Class 对象.
     * @return 返回存储了可处理方法的集合.
     */
    private Set<Method> findHandleMethod(Class<? extends EventObject> eventClass) {
        Set<Method> methods = new HashSet<>();
        for (Class<? extends EventObject> clazz : eventHandlerMap.keySet()) {
            if (!clazz.isAssignableFrom(eventClass)) {
                continue;
            }

            Set<Method> eventHandlers = eventHandlerMap.get(clazz);
            for (Method handlerMethod : eventHandlers) {
                EventHandler handlerInfo = handlerMethod.getAnnotation(EventHandler.class);
                if (handlerInfo.inheritable()) {
                    methods.add(handlerMethod);
                }
            }
        }
        return methods;
    }

}
