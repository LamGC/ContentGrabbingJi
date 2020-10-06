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

import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.convert.StringToStringConverter;
import net.lamgc.cgj.bot.cache.local.CopyOnWriteArrayListCacheStore;
import net.lamgc.cgj.bot.cache.local.HashSetCacheStore;
import net.lamgc.cgj.bot.cache.redis.RedisMapCacheStore;
import net.lamgc.cgj.bot.cache.redis.RedisSingleCacheStore;
import org.junit.Assert;
import org.junit.Test;

/**
 * @see CacheStoreBuilder
 */
public class CacheStoreBuilderTest {

    @Test
    public void getCacheStoreTest() {
        final String identify = "test";
        final StringConverter<String> converter = new StringToStringConverter();

        SingleCacheStore<String> singleCacheStore = CacheStoreBuilder.newSingleCacheStore(CacheStoreSource.REMOTE, identify, converter);
        Assert.assertNotNull(singleCacheStore);
        Assert.assertEquals(RedisSingleCacheStore.class, singleCacheStore.getClass());

        ListCacheStore<String> listCacheStore = CacheStoreBuilder.newListCacheStore(CacheStoreSource.MEMORY, identify, converter);
        Assert.assertNotNull(listCacheStore);
        Assert.assertEquals(CopyOnWriteArrayListCacheStore.class, listCacheStore.getClass());

        MapCacheStore<String> mapCacheStore = CacheStoreBuilder.newMapCacheStore(CacheStoreSource.REMOTE, identify, converter);
        Assert.assertNotNull(mapCacheStore);
        Assert.assertEquals(RedisMapCacheStore.class, mapCacheStore.getClass());

        SetCacheStore<String> setCacheStore = CacheStoreBuilder.newSetCacheStore(identify, converter);
        Assert.assertNotNull(setCacheStore);
        Assert.assertEquals(HashSetCacheStore.class, setCacheStore.getClass());
    }


}
