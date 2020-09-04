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

import net.lamgc.cgj.bot.cache.MapCacheStore;

import java.util.*;
import java.util.function.Function;

/**
 *
 * @param <V>
 * @see net.lamgc.cgj.bot.cache.CacheStore
 * @see net.lamgc.cgj.bot.cache.MapCacheStore
 * @author LamGC
 */
public class HashMapCacheStore<V> extends HashCacheStore<Map<String, V>> implements MapCacheStore<V> {

    @Override
    public int mapSize(String key) {
        return getMap(key, false, Map::size, -1);
    }

    @Override
    public Set<String> mapFieldSet(String key) {
        return getMap(key, false, Map::keySet, null);
    }

    @Override
    public Set<V> mapValueSet(String key) {
        return getMap(key, false, map -> new HashSet<>(map.values()), null);
    }

    @Override
    public boolean put(String key, String field, V value) {
        return getMap(key, true, map -> {
            map.put(Objects.requireNonNull(field), Objects.requireNonNull(value));
            return true;
        }, false);
    }

    @Override
    public boolean putAll(String key, Map<String, V> map) {
        return getMap(key, true, keyMap -> {
            keyMap.putAll(Objects.requireNonNull(map));
            return true;
        }, false);
    }

    @Override
    public boolean putIfNotExist(String key, String field, V value) {
        return getMap(key, true, map -> {
            if (map.containsKey(Objects.requireNonNull(field))) {
                return false;
            }
            map.put(Objects.requireNonNull(field), Objects.requireNonNull(value));
            return true;
        }, false);
    }

    @Override
    public V get(String key, String field) {
        return getMap(key, false, map -> map.get(Objects.requireNonNull(field)), null);
    }

    @Override
    public boolean removeField(String key, String field) {
        return getMap(key, false, map -> map.remove(Objects.requireNonNull(field)) != null, false);
    }

    @Override
    public boolean containsField(String key, String field) {
        return getMap(key, false, map -> map.containsKey(Objects.requireNonNull(field)), false);
    }

    @Override
    public boolean mapIsEmpty(String key) {
        return getMap(key, false, Map::isEmpty, false);
    }

    @Override
    public boolean clearMap(String key) {
        return getMap(key, false, map -> {
            map.clear();
            return true;
        }, false);
    }

    private <R> R getMap(String key, boolean create, Function<Map<String, V>, R> notNull, R isNull) {
        Objects.requireNonNull(key);
        Map<String, CacheItem<Map<String, V>>> cacheMap = getCacheMap();
        if (!cacheMap.containsKey(key)) {
            if (create) {
                cacheMap.put(key, new CacheItem<>(new Hashtable<>()));
            } else {
                return isNull;
            }
        }
        return notNull.apply(cacheMap.get(key).getValue());
    }

}
