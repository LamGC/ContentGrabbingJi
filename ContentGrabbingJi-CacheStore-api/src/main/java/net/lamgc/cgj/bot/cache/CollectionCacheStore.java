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

import java.util.Collection;

/**
 * 集合型缓存存储容器.
 * @param <E> 元素类型.
 * @author LamGC
 */
public interface CollectionCacheStore<E> extends CacheStore<Collection<E>> {

    /**
     * 为缓存项添加一个元素.
     * 当缓存项不存在时, 将会创建一个新的缓存项.
     * @param key 缓存项键名.
     * @param value 待添加的元素.
     * @return 如果成功返回 true.
     * @throws NullPointerException 当 key 或 value 为 null 时抛出; 本方法不允许存储 null 值, 因为 null 代表"没有/不存在".
     */
    boolean addElement(String key, E value);

    /**
     * 为缓存项添加一组元素.
     * 当缓存项不存在时, 将会创建一个新的缓存项.
     * @param key 缓存项键名.
     * @param values 欲添加的元素集合.
     * @return 如果成功添加, 返回 true, 如果无法添加(例如缓存项 List/Set 长度限制), 返回 false.
     * @throws NullPointerException 当 key 或 value 为 null 时抛出; 本方法不允许存储 null 值, 因为 null 代表"没有/不存在".
     */
    boolean addElements(String key, Collection<E> values);

    /**
     * 检查指定元素是否包含在指定缓存项中.
     * @param key 待检查的缓存项键名.
     * @param value 待查找的缓存值.
     * @return 如果存在, 返回 true, 如果元素不存在, 或缓存项不存在, 返回 false.
     * @throws NullPointerException 当 key 或 value 为 null 时抛出; 本方法不允许存储 null 值, 因为 null 代表"没有/不存在".
     */
    boolean containsElement(String key, E value);

    /**
     * 检查指定缓存项是否为空.
     * @param key 待检查的缓存项键名.
     * @return 如果缓存项无元素, 返回 true, 等效于 {@code elementsLength(key) == 0}
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    boolean isEmpty(String key);

    /**
     * 获取指定缓存项中的元素数量.
     * @param key 待获取元素的缓存项键名.
     * @return 返回指定缓存项中的元素数量, 如果缓存项不存在, 返回 -1.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    int elementsLength(String key);

    /**
     * 清空集合中的所有元素.
     * @param key 欲清空集合的缓存项键名.
     * @return 操作成功返回 true.
     */
    boolean clearCollection(String key);

    /**
     * 删除缓存项中指定的元素.
     * 该方法与 {@link CacheStore#remove(String)} 不同, 该方法仅删除缓存项中的指定元素, 即使删除后缓存项中没有元素, 也不会删除缓存项.
     * @param key 待操作的缓存项键名.
     * @param element 欲删除的元素.
     * @return 如果元素存在且删除成功, 返回 true.
     */
    boolean removeElement(String key, E element);

}
