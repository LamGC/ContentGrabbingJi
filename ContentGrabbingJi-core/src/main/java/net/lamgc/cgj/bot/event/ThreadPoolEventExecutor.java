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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 基于线程池, 以事件为单位的异步事件执行器.
 * <p> 以事件为单位对于 ContentGrabbingJi 来说是实现难度低, 还很合适的,
 * ContentGrabbingJi 只会注册一个 Handler, 所以依然类似于一个 Handler 一个线程.
 * @author LamGC
 */
public class ThreadPoolEventExecutor implements EventExecutor {

    private final ThreadPoolExecutor threadExecutor;
    private final EventHandlerRegistry registry;

    /**
     * 构造线程池事件执行器.
     * @param threadExecutor 执行器所使用的线程池.
     * @param registry 事件处理注册器.
     */
    public ThreadPoolEventExecutor(ThreadPoolExecutor threadExecutor, EventHandlerRegistry registry) {
        this.threadExecutor = threadExecutor;
        this.registry = registry;
    }

    @Override
    public void execute(EventObject event) {
        threadExecutor.execute(new ExecuteRunnable(event, registry.getMatchedHandlerMethod(event)));
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    /**
     * 按事件为单位的事件处理执行类.
     */
    private final static class ExecuteRunnable implements Runnable {

        private final static Logger log = LoggerFactory.getLogger(ExecuteRunnable.class);

        private final EventObject event;
        private final Map<Method, Object> handlerMethods;

        private ExecuteRunnable(EventObject event, Map<Method, Object> handlerMethods) {
            this.event = Objects.requireNonNull(event);
            this.handlerMethods = Objects.requireNonNull(handlerMethods);
        }

        @Override
        public void run() {
            if (event instanceof Cancelable) {
                runCancelableEvent();
            } else {
                runOrdinaryEvent();
            }
        }

        /**
         * 运行可取消事件.
         */
        private void runCancelableEvent() {
            Cancelable cancelable = (Cancelable) event;
            for (Method handlerMethod : handlerMethods.keySet()) {
                if (cancelable.canceled()) {
                    log.warn("事件 {} 已取消, 终止处理.", event);
                    break;
                }

                try {
                    Object instance = handlerMethods.get(handlerMethod);
                    checkInstance(handlerMethod, instance);
                    handlerMethod.invoke(instance, event);
                } catch (InvocationTargetException e) {
                    log.error("Handler '" + handlerMethod.getDeclaringClass() + "." +
                            handlerMethod.getName() + "()'" +" throws an uncaught exception when handling an event",
                            e);
                } catch (Exception e) {
                    log.error("Exception in handler '" + handlerMethod.getDeclaringClass() + "." +
                            handlerMethod.getName() + "()' call", e);
                }
            }
        }

        /**
         * 运行普通事件.
         */
        private void runOrdinaryEvent() {
            handlerMethods.forEach((method, instance) -> {
                try {
                    checkInstance(method, instance);
                    method.invoke(instance, event);
                } catch (InvocationTargetException e) {
                    log.error("Handler '" + method.getDeclaringClass() + "." + method.getName() + "()'" +
                            " throws an uncaught exception when handling an event", e);
                } catch (Exception e) {
                    log.error("Exception in handler '" + method.getDeclaringClass() + "." +
                            method.getName() + "()' call", e);
                }
            });
        }

        private void checkInstance(Method method, Object object) {
            if (!method.getDeclaringClass().isAssignableFrom(object.getClass())) {
                throw new ClassCastException("Method declaration class does not match call instance (Method: '" +
                        method.getDeclaringClass().getName() + "', Instance: '" + object.getClass().getName() + "')");
            }
        }

    }

}
