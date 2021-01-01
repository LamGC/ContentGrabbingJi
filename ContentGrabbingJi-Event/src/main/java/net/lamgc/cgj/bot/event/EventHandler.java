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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件处理器注解.
 * <p> 标记了该注解的方法, 如符合处理方法条件, 则会被 {@link HandlerRegistry} 注册为事件处理方法.
 * @author LamGC
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    /**
     * 是否接受事件对象的子类.
     * <p> 当该选项为 {@code true} 时, 子类事件也会传递到该方法进行处理.
     *     例如事件 Parent, 和它的子类 Child, 处理方法: {@code handler(Parent event)};
     *     如果该选项为 {@code true}, 那么 Child 也会传递到该方法进行处理, 反之, 如果该选项为 {@code false},
     *     那么该方法只会接收 Parent 事件, 而不会接收它的子类 Child.
     * <p> 默认值: {@code false}
     * @return 返回该方法是否支持继承性.
     */
    boolean inheritable() default false;

}
