/*
 * Copyright (C) 2021  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
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
import net.lamgc.cgj.bot.cache.exception.GetCacheStoreException;

import java.io.File;

/**
 * 缓存存储容器构造工厂.
 *
 * <p>可支持不同实现缓存存储容器.
 * @author LamGC
 */
public interface CacheStoreFactory {

    /**
     * Factory 初始化方法.
     * <p> 当 Factory 被加载且验证正确后, 将会在第一次获取 CacheStore 前被调用一次(也可能在验证完成后执行).
     * <p> 该方法仅调用一次, 之后将不再调用.
     * @param dataDirectory 数据存储目录, 可在该目录内存储配置, 或持久化缓存.
     * @throws Exception 上层管理类允许捕获任何抛出的异常, 如果 Factory 抛出异常, 将视为失效, 不被使用.
     */
    void initial(File dataDirectory) throws Exception;

    /**
     * 获取一个新的 CacheStore 对象.
     * @param identify 缓存标识.
     * @param converter 类型的转换器.
     * @return 返回 CacheStore 对象.
     * @throws GetCacheStoreException 当 Factory 无法返回 CacheStore 对象时抛出, 需说明失败原因.
     */
    <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) throws GetCacheStoreException;

    /**
     * 获取一个新的有序列表缓存存储容器.
     * @param identify 缓存标识.
     * @param converter 元素类型与 String 的转换器.
     * @param <E> 元素类型.
     * @return 返回新的有序列表缓存存储容器.
     * @throws GetCacheStoreException 当 Factory 无法返回 CacheStore 对象时抛出, 需说明失败原因.
     */
    <E> ListCacheStore<E> newListCacheStore(String identify, StringConverter<E> converter) throws GetCacheStoreException;

    /**
     * 获取一个新的无序集合缓存存储容器.
     * @param identify 缓存标识.
     * @param converter 元素类型与 String 的转换器.
     * @param <E> 元素类型.
     * @return 返回新的无序集合缓存存储容器.
     * @throws GetCacheStoreException 当 Factory 无法返回 CacheStore 对象时抛出, 需说明失败原因.
     */
    <E> SetCacheStore<E> newSetCacheStore(String identify, StringConverter<E> converter) throws GetCacheStoreException;

    /**
     * 获取一个新的映射表缓存存储容器.
     * @param identify 缓存标识
     * @param converter 字段值类型与 String 的转换器.
     * @param <V> 字段值类型.
     * @return 返回新的映射表缓存存储容器.
     * @throws GetCacheStoreException 当 Factory 无法返回 CacheStore 对象时抛出, 需说明失败原因.
     */
    <V> MapCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter) throws GetCacheStoreException;

    /**
     * 当前是否可以创建 {@link CacheStore}
     *
     * <p> 如果返回 true, 将会使用该 Factory.
     *     如果返回 false 或抛出异常, 将不会通过该 Factory 创建 CacheStore,
     *
     * <p> 除非模块能保证 Factory 正常情况下一定能提供 CacheStore 对象,
     *     否则请不要尝试永远返回 true 来向应用保证 Factory 一定能创建 CacheStore, 保持 Factory 的有效性,
     *     一旦后续创建 CacheStore 时发生异常, 将视为无法创建.
     * @return 如果可以, 返回 true.
     */
    boolean canGetCacheStore();

}
