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
 * 单项存取的缓存存储容器.
 * <p>该缓存存储容器在存储上, 一个键对应一个值, 不存在一对多的情况.
 * @param <V> 值类型.
 * @author LamGC
 */
public interface SingleCacheStore<V> extends CacheStore<V> {

    /**
     * 设置指定键为指定值.
     * 如果缓存项不存在, 则新建缓存并存储, 如果存在, 则覆盖原缓存;
     * <p>覆盖缓存相当于是"删除"该缓存并重新创建, "删除"意味着原缓存项的相关设置将会丢失(例如"过期时间").
     * @param key 缓存项键名.
     * @param value 缓存值.
     * @return 如果成功返回 true.
     * @throws NullPointerException 当 key 或 value 为 null 时抛出; 本方法不允许存储 null 值, 因为 null 代表"没有/不存在".
     */
    boolean set(CacheKey key, V value);

    /**
     * 设置指定键为指定值.
     * <p>该方法与 {@link #set(CacheKey, Object)} 类似, 但如果该 key 已经存在缓存, 则不执行 set 操作并返回 false.
     * @param key 缓存项键名.
     * @param value 缓存值.
     * @return 如果成功返回 true, 当 key 已存在, 或设置失败时返回 false.
     * @throws NullPointerException 当 key 或 value 为 null 时抛出; 本方法不允许存储 null 值, 因为 null 代表"没有/不存在".
     */
    boolean setIfNotExist(CacheKey key, V value);

    /**
     * 获取缓存项值.
     * @param key 欲取值的缓存项键名.
     * @return 如果缓存项存在, 返回缓存项的值, 否则返回 null.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    V get(CacheKey key);

}
