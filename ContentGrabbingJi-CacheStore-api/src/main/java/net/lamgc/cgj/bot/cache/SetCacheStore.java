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

import java.util.Set;

/**
 * 无序集合的缓存存储容器.
 * <p>其中, 元素是唯一的, 不会出现重复情况.
 * @param <E> 值类型.
 * @author LamGC
 */
public interface SetCacheStore<E> extends CollectionCacheStore<E, Set<E>> {
    /*
     * 说实话, SetCacheStore 的存在有点...奇怪, 或者可能就没有用,
     * 因为根据 2 代对缓存存储的使用情况来看, 用到 Set 的地方根本就没有,
     * 而且可能在某些方面实现起来也不是件容易事情.
     * 所以 SetCacheStore 可能会废弃掉(最晚也会在正式版出来前),
     * 或者有用的话才会为其设计相关方法.
     * (比如偏向 Redis 的那套进行设计, 但是我希望不要出现偏向性, 为所有实现提供一个平衡的实现复杂度)
     */
}
