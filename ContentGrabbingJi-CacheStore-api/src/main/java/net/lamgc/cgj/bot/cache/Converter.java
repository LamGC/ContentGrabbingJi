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

package net.lamgc.cgj.bot.cache;

/**
 * 转换器接口
 * @param <S> 源类型
 * @param <T> 目标类型
 * @author LamGC
 */
public interface Converter<S, T> {

    /**
     * 将源类型对象转换成目标类型.
     * @param source 源类型对象.
     * @return 对应的目标类型对象.
     */
    T to(S source);

    /**
     * 从目标类型对象转换回源类型.
     * @param target 目标类型对象.
     * @return 对应的源类型对象.
     */
    S from(T target);

}
