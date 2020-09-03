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

import net.lamgc.cgj.bot.cache.convert.StringConverter;

/**
 * 缓存存储容器构造工厂.
 * 可支持不同实现缓存存储容器.
 * @author LamGC
 */
public interface CacheStoreFactory {

    /**
     * 获取一个新的 CacheStore 对象.
     * @param identify 缓存标识.
     * @param converter 类型的转换器.
     * @return 返回 CacheStore 对象.
     */
    <V> SingleCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter);

    /**
     * 获取一个新的有序列表缓存存储容器.
     * @param identify 缓存标识.
     * @param converter 元素类型与 String 的转换器.
     * @param <V> 元素类型.
     * @return 返回新的有序列表缓存存储容器.
     */
    <V> ListCacheStore<V> newListCacheStore(String identify, StringConverter<V> converter);

    /**
     * 获取一个新的无序集合缓存存储容器.
     * @param identify 缓存标识.
     * @param converter 元素类型与 String 的转换器.
     * @param <V> 元素类型.
     * @return 返回新的无序集合缓存存储容器.
     */
    <V> SetCacheStore<V> newSetCacheStore(String identify, StringConverter<V> converter);

}
