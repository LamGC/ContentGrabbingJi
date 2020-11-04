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

package net.lamgc.cgj.bot.framework;

import net.lamgc.cgj.bot.cache.CacheStoreBuilder;
import net.lamgc.cgj.bot.event.EventExecutor;

/**
 * 框架上下文对象.
 * <p> 由于 ContentGrabbingJi 按实例分配资源,
 * 故 Context 可获取专属于框架所属实例的相关可用资源以供框架使用.
 * @author LamGC
 */
public interface FrameworkContext {

    /**
     * 获取事件执行器.
     * <p> 事件执行器用于将事件提交给 CGJ 进行处理.
     * @return 返回事件执行器.
     */
    EventExecutor getEventExecutor();

    /**
     * 获取缓存存储容器构造器.
     * <p> 如需使用缓存, 可通过 {@link CacheStoreBuilder} 获取独立的缓存容器.
     * @return 返回构造器.
     */
    CacheStoreBuilder getCacheStoreBuilder();

}
