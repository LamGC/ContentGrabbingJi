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

import java.util.List;

/**
 * List 类型的缓存存储容器.
 * <p>该存储容器内的 List 与 {@link List} 概念相同, 即"有序列表".
 * @param <E> 值类型.
 * @author LamGC
 */
public interface ListCacheStore<E> extends CollectionCacheStore<E, List<E>> {

    /**
     * 获取缓存项中的指定元素.
     * @param key 欲取值的缓存项键名.
     * @param index 元素索引, 从 0 开始.
     * @return 如果缓存项存在, 且指定索引存在元素, 则返回元素,
     *         如果缓存项不存在, 或索引超出范围(超出长度或低于 0)等导致获取失败, 返回 null.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    E getElement(String key, int index);

    /**
     * 根据返回获取部分元素.
     * @param key 欲取值的缓存项键值.
     * @param index 起始元素索引, 从 0 开始.
     * @param length 获取长度.
     * @return 如果成功, 返回成功获取到的元素数量, 失败返回 null.
     *         <p>对于起始元素索引超出范围, 或获取长度与实际能获取的长度不同的情况, 不允许返回 null,
     *         相反, 返回的 List 中的元素数量应反映实际所可获取的元素数量, 例如:
     *         <ul>
     *             <li>因 index 超出范围(包括 index 低于 0, 也是如此)而无法获取元素, 则返回无元素 List 对象;
     *             <li>如果从起始元素开始获取无法获取 length 数量的元素, 则直接返回包含已成功获取元素的 List 对象.
     *         </ul>
     *         但需要注意的是: 获取异常不包括在上述范围, 因此如果在获取中出现错误导致获取失败, 应以失败返回 null.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    List<E> getElementsByRange(String key, int index, int length);

    /**
     * 删除指定索引的元素.
     *
     * <p>该方法与 {@link CacheStore#remove(String)} 不同, 该方法仅删除缓存项中的指定元素, 即使删除后缓存项中没有元素, 也不会删除缓存项.
     * @param key 待操作的缓存项键名.
     * @param index 欲删除元素的索引, 从 0 开始.
     * @return 如果元素存在且删除成功, 返回 true.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    boolean removeElement(String key, int index);

    /**
     * 删除缓存项中指定的元素.
     * <p>当 List 存在多个该元素时, 删除第一个匹配到的元素.
     * <p>该方法与 {@link CacheStore#remove(String)} 不同, 该方法仅删除缓存项中的指定元素, 即使删除后缓存项中没有元素, 也不会删除缓存项.
     * @param key 待操作的缓存项键名.
     * @param element 欲删除的元素.
     * @return 如果元素存在且删除成功, 返回 true.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    @Override
    boolean removeElement(String key, E element);
}
