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

package net.lamgc.cgj.bot.cache.local;

import net.lamgc.cgj.bot.cache.CacheKey;
import net.lamgc.cgj.bot.cache.ListCacheStore;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于 {@link CopyOnWriteArrayList} 的有序列表缓存存储容器.
 * @param <E> 元素类型.
 * @author LamGC
 */
public class CopyOnWriteArrayListCacheStore<E>
        extends LocalCollectionCacheStore<E, List<E>>
        implements ListCacheStore<E>
{

    @Override
    public E getElement(CacheKey key, int index) {
        List<E> itemCollection = getCacheItemCollection(key, false);
        try {
            return itemCollection == null ? null : itemCollection.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public List<E> getElementsByRange(CacheKey key, int index, int length) {
        int listLength = elementsLength(key);
        if (listLength == -1) {
            return null;
        }
        List<E> itemCollection = getCacheItemCollection(key, false);
        List<E> result = new ArrayList<>();

        try {
            ListIterator<E> iterator = itemCollection.listIterator(index);
            for (int i = 0; i < length && iterator.hasNext(); i++) {
                result.add(iterator.next());
            }
        } catch (IndexOutOfBoundsException ignored) {
            // 正常情况来讲, 该 try-catch 块只有 listIterator 会抛出 IndexOutOfBoundsException,
            // 而一旦抛出 IndexOutOfBoundsException, 就代表 index 溢出了, try 块后面代码没有继续执行,
            // 既然抛出异常时, result 并没有添加任何元素, 为何要再 new 一个 List 浪费内存呢? :D
        }
        return result;
    }

    @Override
    public boolean removeElement(CacheKey key, int index) {
        List<E> itemCollection = getCacheItemCollection(key, false);
        if (itemCollection != null) {
            try {
                itemCollection.remove(index);
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected List<E> getCacheItemCollection(CacheKey key, boolean create) {
        Objects.requireNonNull(key);
        Map<String, CacheItem<List<E>>> cacheMap = getCacheMap();
        if (!cacheMap.containsKey(key.toString())) {
            if (create) {
                cacheMap.put(key.toString(), new CacheItem<>(new CopyOnWriteArrayList<>()));
            } else {
                return null;
            }
        }
        return cacheMap.get(key.toString()).getValue();
    }
}
