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

import net.lamgc.cgj.bot.cache.CacheKey;
import net.lamgc.cgj.bot.cache.SetCacheStore;

import java.util.*;

/**
 *
 * @param <E> 元素类型.
 * @author LamGC
 */
public class HashSetCacheStore<E> extends LocalCollectionCacheStore<E, Set<E>> implements SetCacheStore<E> {

    @Override
    protected Set<E> getCacheItemCollection(CacheKey key, boolean create) {
        Objects.requireNonNull(key);
        String keyString = key.toString();
        Map<String, CacheItem<Set<E>>> cacheMap = getCacheMap();
        if (!cacheMap.containsKey(keyString)) {
            if (create) {
                cacheMap.put(keyString, new CacheItem<>(new HashSet<>()));
            } else {
                return null;
            }
        }
        return cacheMap.get(keyString).getValue();
    }

}
