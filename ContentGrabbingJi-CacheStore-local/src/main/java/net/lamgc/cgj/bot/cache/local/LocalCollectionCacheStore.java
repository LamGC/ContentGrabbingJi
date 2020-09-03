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

package net.lamgc.cgj.bot.cache.local;

import net.lamgc.cgj.bot.cache.CollectionCacheStore;

import java.util.Collection;
import java.util.Objects;

/**
 * 本地集合缓存存储容器.
 * @param <E> 元素类型
 * @author LamGC
 * @see net.lamgc.cgj.bot.cache.CacheStore
 * @see net.lamgc.cgj.bot.cache.CollectionCacheStore
 */
public abstract class LocalCollectionCacheStore<E, C extends Collection<E>>
extends HashCacheStore<C>
implements CollectionCacheStore<E, C> {

    /**
     * 获取缓存项集合对象.
     * @param key 缓存项键名
     * @param create 如果不存在, 是否创建.
     * @return 如果不存在且 create 为 false, 或添加失败, 返回 false, 添加成功返回 true.
     */
    protected abstract C getCacheItemCollection(String key, boolean create);

    @Override
    public boolean addElement(String key, E element) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(element);
        Collection<E> itemCollection = getCacheItemCollection(key, true);
        return itemCollection.add(element);
    }

    @Override
    public boolean addElements(String key, Collection<E> elements) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(elements);
        Collection<E> itemCollection = getCacheItemCollection(key, true);
        return itemCollection.addAll(elements);
    }

    @Override
    public boolean containsElement(String key, E value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Collection<E> itemCollection = getCacheItemCollection(key, false);
        if (itemCollection == null) {
            return false;
        }
        return itemCollection.contains(value);
    }

    @Override
    public boolean isEmpty(String key) {
        Collection<E> itemCollection = getCacheItemCollection(Objects.requireNonNull(key), false);
        if (itemCollection == null) {
            return false;
        }
        return itemCollection.isEmpty();
    }

    @Override
    public int elementsLength(String key) {
        Collection<E> itemCollection = getCacheItemCollection(Objects.requireNonNull(key), false);
        if (itemCollection == null) {
            return -1;
        }
        return itemCollection.size();
    }

    @Override
    public boolean clearCollection(String key) {
        Collection<E> itemCollection = getCacheItemCollection(Objects.requireNonNull(key), false);
        if (itemCollection == null) {
            return false;
        }
        itemCollection.clear();
        return true;
    }

    @Override
    public boolean removeElement(String key, E element) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(element);
        Collection<E> itemCollection = getCacheItemCollection(key, false);
        if (itemCollection == null) {
            return false;
        }
        return itemCollection.remove(element);
    }

}
