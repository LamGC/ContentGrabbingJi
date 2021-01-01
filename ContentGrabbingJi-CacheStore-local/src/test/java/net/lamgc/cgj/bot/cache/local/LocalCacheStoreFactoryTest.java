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

import net.lamgc.cgj.bot.cache.ListCacheStore;
import net.lamgc.cgj.bot.cache.MapCacheStore;
import net.lamgc.cgj.bot.cache.SetCacheStore;
import net.lamgc.cgj.bot.cache.SingleCacheStore;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.convert.StringToStringConverter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

/**
 * @see LocalCacheStoreFactory
 */
public class LocalCacheStoreFactoryTest {

    private final static TemporaryFolder tempFolder = TemporaryFolder.builder()
            .build();

    private final static String IDENTIFY = "test";
    private final static StringConverter<String> CONVERTER = new StringToStringConverter();
    private static File storeFolder;

    @BeforeClass
    public static void beforeProcess() throws IOException {
        tempFolder.create();
        storeFolder = tempFolder.newFolder("cache", "Local-Memory");
        Assert.assertNotNull(createLocalCacheStoreFactory());
    }

    private static LocalCacheStoreFactory createLocalCacheStoreFactory() {
        LocalCacheStoreFactory factory = new LocalCacheStoreFactory();
        Assert.assertNotNull(storeFolder);
        factory.initial(storeFolder);
        return factory;
    }

    @Test
    public void initial() {
        Assert.assertNotNull(createLocalCacheStoreFactory());
    }

    @Test
    public void newSingleCacheStore() {
        SingleCacheStore<String> cacheStore = createLocalCacheStoreFactory().newSingleCacheStore(IDENTIFY, CONVERTER);
        Assert.assertEquals(HashSingleCacheStore.class, cacheStore.getClass());
    }

    @Test
    public void newListCacheStore() {
        ListCacheStore<String> cacheStore = createLocalCacheStoreFactory().newListCacheStore(IDENTIFY, CONVERTER);
        Assert.assertEquals(CopyOnWriteArrayListCacheStore.class, cacheStore.getClass());
    }

    @Test
    public void newSetCacheStore() {
        SetCacheStore<String> cacheStore = createLocalCacheStoreFactory().newSetCacheStore(IDENTIFY, CONVERTER);
        Assert.assertEquals(HashSetCacheStore.class, cacheStore.getClass());
    }

    @Test
    public void newMapCacheStore() {
        MapCacheStore<String> cacheStore = createLocalCacheStoreFactory().newMapCacheStore(IDENTIFY, CONVERTER);
        Assert.assertEquals(HashMapCacheStore.class, cacheStore.getClass());
    }

    @Test
    public void canGetCacheStore() {
        Assert.assertTrue(createLocalCacheStoreFactory().canGetCacheStore());
    }
}