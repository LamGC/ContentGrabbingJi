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

import com.google.common.base.Throwables;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.convert.StringToStringConverter;
import net.lamgc.cgj.bot.cache.local.CopyOnWriteArrayListCacheStore;
import net.lamgc.cgj.bot.cache.local.HashSetCacheStore;
import net.lamgc.cgj.bot.cache.redis.RedisMapCacheStore;
import net.lamgc.cgj.bot.cache.redis.RedisSingleCacheStore;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

/**
 * @see CacheStoreBuilder
 */
public class CacheStoreBuilderTest {

    private final static TemporaryFolder tempDirectory = TemporaryFolder.builder().build();

    @BeforeClass
    public static void beforeAction() {
        try {
            tempDirectory.create();
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
    }

    @Test
    public void getCacheStoreTest() {
        final String identify = "test";
        final StringConverter<String> converter = new StringToStringConverter();
        CacheStoreBuilder cacheStoreBuilder;
        try {
            cacheStoreBuilder = CacheStoreBuilder.getInstance(tempDirectory.getRoot());
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
            return;
        }

        SingleCacheStore<String> singleCacheStore = cacheStoreBuilder.newSingleCacheStore(CacheStoreSource.REMOTE, identify, converter);
        Assert.assertNotNull(singleCacheStore);
        Assert.assertEquals(RedisSingleCacheStore.class, singleCacheStore.getClass());

        ListCacheStore<String> listCacheStore = cacheStoreBuilder.newListCacheStore(CacheStoreSource.MEMORY, identify, converter);
        Assert.assertNotNull(listCacheStore);
        Assert.assertEquals(CopyOnWriteArrayListCacheStore.class, listCacheStore.getClass());

        MapCacheStore<String> mapCacheStore = cacheStoreBuilder.newMapCacheStore(CacheStoreSource.REMOTE, identify, converter);
        Assert.assertNotNull(mapCacheStore);
        Assert.assertEquals(RedisMapCacheStore.class, mapCacheStore.getClass());

        SetCacheStore<String> setCacheStore = cacheStoreBuilder.newSetCacheStore(identify, converter);
        Assert.assertNotNull(setCacheStore);
        Assert.assertEquals(HashSetCacheStore.class, setCacheStore.getClass());
    }


}
