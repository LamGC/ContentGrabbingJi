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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LamGC
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Factory {

    /**
     * Cache 模块名称.
     * @return 返回实现模块名称.
     */
    String name();

    /**
     * CacheStore 优先级.
     * <p>最终所使用的 CacheStoreFactory 将会根据其优先级进行选择.
     * 当优先级高的 Factory 表示无法创建 CacheStore 时, 将会寻找比该 Factory 优先级较低的下一个 Factory 并尝试获取,
     * 重复该过程直到找到能使用的 Factory, 或者使用缺省的 CacheStore-local.
     *
     * <p>注意: 即使优先级超过 {@linkplain FactoryPriority#PRIORITY_HIGHEST 10} 也会被视为 10,
     * 同样的, 即使优先级低于 {@linkplain FactoryPriority#PRIORITY_LOWEST 0} 也会被视为 0.
     * @return 返回优先级, 最低优先级为 0, 优先级越高, 越会优先选择, 除非无法使用.
     */
    int priority() default FactoryPriority.PRIORITY_NORMAL;

}
