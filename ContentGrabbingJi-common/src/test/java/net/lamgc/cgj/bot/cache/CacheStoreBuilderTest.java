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
import net.lamgc.cgj.bot.cache.exception.GetCacheStoreException;
import net.lamgc.cgj.bot.cache.factory.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @see CacheStoreBuilder
 */
public class CacheStoreBuilderTest {

    private final static TemporaryFolder tempDirectory = TemporaryFolder.builder().build();
    private final static Logger log = LoggerFactory.getLogger(CacheStoreBuilderTest.class);

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
        Assert.assertEquals(RemoteCacheFactory.RemoteSingleCacheFactory.class, singleCacheStore.getClass());

        ListCacheStore<String> listCacheStore = cacheStoreBuilder.newListCacheStore(CacheStoreSource.MEMORY, identify, converter);
        Assert.assertNotNull(listCacheStore);
        Assert.assertEquals(MemoryFactory.MemoryListCacheStore.class, listCacheStore.getClass());

        MapCacheStore<String> mapCacheStore = cacheStoreBuilder.newMapCacheStore(CacheStoreSource.LOCAL, identify, converter);
        Assert.assertNotNull(mapCacheStore);
        Assert.assertEquals(LocalFactory.LocalMapCacheStore.class, mapCacheStore.getClass());

        SetCacheStore<String> setCacheStore = cacheStoreBuilder.newSetCacheStore(identify, converter);
        Assert.assertNotNull(setCacheStore);
        Assert.assertEquals(SetCacheStoreFactory.OnlySetCacheStore.class, setCacheStore.getClass());
    }

    @Test
    public void loadFailureTest() throws IllegalAccessException, NoSuchFieldException {
        CacheStoreBuilder cacheStoreBuilder;
        try {
            cacheStoreBuilder = CacheStoreBuilder.getInstance(tempDirectory.getRoot());
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
            return;
        }

        Field factoryListField;
        factoryListField = CacheStoreBuilder.class.getDeclaredField("factoryList");


        factoryListField.setAccessible(true);
        Object o = factoryListField.get(cacheStoreBuilder);
        Assert.assertTrue(o instanceof List);
        @SuppressWarnings("unchecked")
        List<CacheStoreFactory> factoryList = (List<CacheStoreFactory>) o;
        Set<Class<? extends CacheStoreFactory>> classSet = new HashSet<>();
        factoryList.forEach(factory -> classSet.add(factory.getClass()));

        // 重名检查
        if (classSet.contains(DuplicateNameFactoryA.class) && classSet.contains(DuplicateNameFactoryB.class)) {
            Assert.fail("There are different factories with the same name");
            return;
        }

        if (classSet.contains(NoAnnotationFactory.class)) {
            Assert.fail("Factory without @Factory added is loaded");
            return;
        }

        if (classSet.contains(InitialFailureFactory.class)) {
            Assert.fail("The factory that failed to initialize was loaded");
        }

    }

    @Test
    public void multiThreadReloadTest() throws IOException, NoSuchMethodException, InterruptedException {
        final String identify = "test";
        final StringConverter<String> converter = new StringToStringConverter();
        final CacheStoreBuilder cacheStoreBuilder = CacheStoreBuilder.getInstance(tempDirectory.getRoot());

        final Method loadFactoryMethod = CacheStoreBuilder.class.getDeclaredMethod("loadFactory");
        loadFactoryMethod.setAccessible(true);

        Thread accessThread = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                SingleCacheStore<String> singleCacheStore = cacheStoreBuilder.newSingleCacheStore(CacheStoreSource.REMOTE, identify, converter);
                Assert.assertNotNull(singleCacheStore);
                Assert.assertEquals(RemoteCacheFactory.RemoteSingleCacheFactory.class, singleCacheStore.getClass());

                ListCacheStore<String> listCacheStore = cacheStoreBuilder.newListCacheStore(CacheStoreSource.MEMORY, identify, converter);
                Assert.assertNotNull(listCacheStore);
                Assert.assertEquals(MemoryFactory.MemoryListCacheStore.class, listCacheStore.getClass());

                MapCacheStore<String> mapCacheStore = cacheStoreBuilder.newMapCacheStore(CacheStoreSource.LOCAL, identify, converter);
                Assert.assertNotNull(mapCacheStore);
                Assert.assertEquals(LocalFactory.LocalMapCacheStore.class, mapCacheStore.getClass());

                SetCacheStore<String> setCacheStore = cacheStoreBuilder.newSetCacheStore(identify, converter);
                Assert.assertNotNull(setCacheStore);
                Assert.assertEquals(SetCacheStoreFactory.OnlySetCacheStore.class, setCacheStore.getClass());
            }
        }, "Thread-AccessBuilder");
        Thread reloadThread = new Thread(() -> {
            int count = 0;
            final Random random = new Random();
            while(count++ < 100000) {
                if (random.nextInt() % 2 == 0) {
                    try {
                        loadFactoryMethod.invoke(cacheStoreBuilder);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("重载 Builder 时发生异常.",
                                e instanceof InvocationTargetException ?
                                        ((InvocationTargetException) e).getTargetException() :
                                        e);
                    }
                }
            }
        }, "Thread-ReloadBuilder");

        accessThread.start();
        reloadThread.start();

        accessThread.join();
        reloadThread.join();
    }

    @Test
    public void noSpecifiedGetCacheStoreTest() {
        final String identify = "test";
        final StringConverter<String> converter = new StringToStringConverter();
        CacheStoreBuilder cacheStoreBuilder;
        try {
            cacheStoreBuilder = CacheStoreBuilder.getInstance(tempDirectory.getRoot());
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
            return;
        }

        SingleCacheStore<String> singleCacheStore = cacheStoreBuilder.newSingleCacheStore(identify, converter);
        Assert.assertNotNull(singleCacheStore);

        ListCacheStore<String> listCacheStore = cacheStoreBuilder.newListCacheStore(identify, converter);
        Assert.assertNotNull(listCacheStore);

        MapCacheStore<String> mapCacheStore = cacheStoreBuilder.newMapCacheStore(identify, converter);
        Assert.assertNotNull(mapCacheStore);

        SetCacheStore<String> setCacheStore = cacheStoreBuilder.newSetCacheStore(identify, converter);
        Assert.assertNotNull(setCacheStore);
    }

    @Test
    public void noSuchFactoryExceptionThrowTest() throws NoSuchFieldException, IllegalAccessException {
        final String identify = "test";
        final StringConverter<String> converter = new StringToStringConverter();
        CacheStoreBuilder cacheStoreBuilder;
        try {
            cacheStoreBuilder = CacheStoreBuilder.getInstance(tempDirectory.getRoot());
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
            return;
        }

        Field factoryListField;
        factoryListField = CacheStoreBuilder.class.getDeclaredField("factoryList");


        factoryListField.setAccessible(true);
        Object o = factoryListField.get(cacheStoreBuilder);
        Assert.assertTrue(o instanceof List);
        @SuppressWarnings("unchecked")
        List<CacheStoreFactory> factoryList = (List<CacheStoreFactory>) o;
        factoryList.clear();


        try {
            cacheStoreBuilder.newSingleCacheStore(identify, converter);
        } catch (GetCacheStoreException e) {
            if (!(e.getCause() instanceof NoSuchFactoryException)) {
                Assert.fail("The exception is not due to NoSuchFactoryException");
            }
        }

        try {
            cacheStoreBuilder.newMapCacheStore(identify, converter);
        } catch (GetCacheStoreException e) {
            if (!(e.getCause() instanceof NoSuchFactoryException)) {
                Assert.fail("The exception is not due to NoSuchFactoryException");
            }
        }

        try {
            cacheStoreBuilder.newListCacheStore(identify, converter);
        } catch (GetCacheStoreException e) {
            if (!(e.getCause() instanceof NoSuchFactoryException)) {
                Assert.fail("The exception is not due to NoSuchFactoryException");
            }
        }

        try {
            cacheStoreBuilder.newSetCacheStore(identify, converter);
        } catch (GetCacheStoreException e) {
            if (!(e.getCause() instanceof NoSuchFactoryException)) {
                Assert.fail("The exception is not due to NoSuchFactoryException");
            }
        }

    }

    @Test
    public void getInstanceFailureTest() throws IOException {
        Assert.assertThrows(IOException.class, () ->
                CacheStoreBuilder.getInstance(tempDirectory.newFile("invalid file.bin")));

        File onlyReadableDirectory = tempDirectory.newFile("onlyReadable");
        // Assert.assertTrue(onlyReadableDirectory.setWritable(false));
        Assert.assertThrows(IOException.class, () ->
                CacheStoreBuilder.getInstance(new File(onlyReadableDirectory, "cache")));

        Assert.assertNotNull(
                CacheStoreBuilder.getInstance(new File(tempDirectory.newFolder("valid directory"), "cache")));

    }

    @Test
    public void lastFactoryThrowExceptionTest() throws NoSuchFieldException, IllegalAccessException {
        final String identify = "test";
        final StringConverter<String> converter = new StringToStringConverter();
        CacheStoreBuilder cacheStoreBuilder;
        try {
            cacheStoreBuilder = CacheStoreBuilder.getInstance(tempDirectory.getRoot());
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
            return;
        }

        Field factoryListField;
        factoryListField = CacheStoreBuilder.class.getDeclaredField("factoryList");


        factoryListField.setAccessible(true);
        Object o = factoryListField.get(cacheStoreBuilder);
        Assert.assertTrue(o instanceof List);
        @SuppressWarnings("unchecked")
        List<CacheStoreFactory> factoryList = (List<CacheStoreFactory>) o;
        factoryList.removeIf(factory -> !(factory instanceof GetCacheStoreExceptionFactory));

        try {
            cacheStoreBuilder.newSingleCacheStore(identify, converter);
        } catch (GetCacheStoreException e) {
            if (!(e.getCause() instanceof NoSuchFactoryException)) {
                Assert.fail("The exception is not due to NoSuchFactoryException");
            }
        }

        try {
            cacheStoreBuilder.newMapCacheStore(identify, converter);
        } catch (GetCacheStoreException e) {
            if (!(e.getCause() instanceof NoSuchFactoryException)) {
                Assert.fail("The exception is not due to NoSuchFactoryException");
            }
        }

        try {
            cacheStoreBuilder.newListCacheStore(identify, converter);
        } catch (GetCacheStoreException e) {
            if (!(e.getCause() instanceof NoSuchFactoryException)) {
                Assert.fail("The exception is not due to NoSuchFactoryException");
            }
        }

        try {
            cacheStoreBuilder.newSetCacheStore(identify, converter);
        } catch (GetCacheStoreException e) {
            if (!(e.getCause() instanceof NoSuchFactoryException)) {
                Assert.fail("The exception is not due to NoSuchFactoryException");
            }
        }
    }



}
