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
import net.lamgc.cgj.bot.cache.SingleCacheStore;

import java.util.Objects;

/**
 * 基于 {@link java.util.Hashtable} 的 Map 缓存存储容器.
 * @param <V> 值类型.
 * @author LamGC
 */
public class HashSingleCacheStore<V> extends HashCacheStore<V> implements SingleCacheStore<V> {

    @Override
    public boolean set(CacheKey key, V value) {
        getCacheMap().put(Objects.requireNonNull(key).toString(), new CacheItem<>(Objects.requireNonNull(value)));
        return true;
    }

    @Override
    public boolean setIfNotExist(CacheKey key, V value) {
        if (exists(key)) {
            return false;
        }
        return set(key, value);
    }

    @Override
    public V get(CacheKey key) {
        if (!exists(key)) {
            return null;
        }
        return getCacheMap().get(key.toString()).getValue();
    }

}
