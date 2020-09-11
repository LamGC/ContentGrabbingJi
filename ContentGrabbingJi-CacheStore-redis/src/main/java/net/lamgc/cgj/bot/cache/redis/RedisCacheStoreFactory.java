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

package net.lamgc.cgj.bot.cache.redis;

import net.lamgc.cgj.bot.cache.*;
import net.lamgc.cgj.bot.cache.convert.StringConverter;

/**
 *
 * @author LamGC
 */
@Factory(name = "Redis")
public class RedisCacheStoreFactory implements CacheStoreFactory {
    @Override
    public <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) {
        return new RedisSingleCacheStore<>(identify, converter);
    }

    @Override
    public <E> ListCacheStore<E> newListCacheStore(String identify, StringConverter<E> converter) {
        return null;
    }

    @Override
    public <E> SetCacheStore<E> newSetCacheStore(String identify, StringConverter<E> converter) {
        return null;
    }

    @Override
    public <V> MapCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter) {
        return new RedisMapCacheStore<>(identify, converter);
    }

    @Override
    public boolean canGetCacheStore() {
        return RedisConnectionPool.available();
    }
}
