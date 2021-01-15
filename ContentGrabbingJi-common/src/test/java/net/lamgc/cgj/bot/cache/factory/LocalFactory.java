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

package net.lamgc.cgj.bot.cache.factory;

import net.lamgc.cgj.bot.cache.*;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.exception.GetCacheStoreException;
import org.junit.Assert;

import java.io.File;
import java.util.Map;
import java.util.Set;

@Factory(name = "test-local", source = CacheStoreSource.LOCAL)
public class LocalFactory implements CacheStoreFactory {
    @Override
    public void initial(File dataDirectory) {
        Assert.assertNotNull(dataDirectory);
    }

    @Override
    public <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) throws GetCacheStoreException {
        return null;
    }

    @Override
    public <E> ListCacheStore<E> newListCacheStore(String identify, StringConverter<E> converter) throws GetCacheStoreException {
        return null;
    }

    @Override
    public <E> SetCacheStore<E> newSetCacheStore(String identify, StringConverter<E> converter) throws GetCacheStoreException {
        return null;
    }

    @Override
    public <V> MapCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter) throws GetCacheStoreException {
        return new LocalMapCacheStore<>();
    }

    @Override
    public boolean canGetCacheStore() {
        return true;
    }

    public static class LocalMapCacheStore<V> implements MapCacheStore<V> {
        @Override
        public int mapSize(CacheKey key) {
            return 0;
        }

        @Override
        public Set<String> mapFieldSet(CacheKey key) {
            return null;
        }

        @Override
        public Set<V> mapValueSet(CacheKey key) {
            return null;
        }

        @Override
        public boolean put(CacheKey key, String field, V value) {
            return false;
        }

        @Override
        public boolean putAll(CacheKey key, Map<? extends String, ? extends V> map) {
            return false;
        }

        @Override
        public boolean putIfNotExist(CacheKey key, String field, V value) {
            return false;
        }

        @Override
        public V get(CacheKey key, String field) {
            return null;
        }

        @Override
        public boolean removeField(CacheKey key, String field) {
            return false;
        }

        @Override
        public boolean containsField(CacheKey key, String field) {
            return false;
        }

        @Override
        public boolean mapIsEmpty(CacheKey key) {
            return false;
        }

        @Override
        public boolean clearMap(CacheKey key) {
            return false;
        }

        @Override
        public boolean setTimeToLive(CacheKey key, long ttl) {
            return false;
        }

        @Override
        public long getTimeToLive(CacheKey key) {
            return 0;
        }

        @Override
        public long size() {
            return 0;
        }

        @Override
        public boolean clear() {
            return false;
        }

        @Override
        public boolean exists(CacheKey key) {
            return false;
        }

        @Override
        public boolean remove(CacheKey key) {
            return false;
        }

        @Override
        public Set<String> keySet() {
            return null;
        }
    }

}
