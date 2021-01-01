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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.ref.ReferenceQueue;

/**
 * @see AutoCleanTimer
 */
public class AutoCleanTimerTest {

    @BeforeClass
    public static void before() throws ClassNotFoundException, InterruptedException {
        Class.forName(AutoCleanTimer.class.getName(), true, ClassLoader.getSystemClassLoader());
        Thread.sleep(150L);
    }

    @Test
    public void addTest() throws InterruptedException {
        HashSingleCacheStore<String> cacheStore = new HashSingleCacheStore<>();
        AutoCleanTimer.add(cacheStore);

        Thread.sleep(300L);
        final CacheKey persistenceKey = new CacheKey("persistenceKey");
        final CacheKey expireKey = new CacheKey("expireKey");
        final String value = "testValue";

        // 过期键与持久键
        cacheStore.set(persistenceKey, value);
        cacheStore.set(expireKey, value);
        cacheStore.setTimeToLive(expireKey, 50);

        Thread.sleep(1000L);

        Assert.assertTrue(cacheStore.exists(persistenceKey));
        Assert.assertFalse(cacheStore.exists(expireKey));
    }

    @Test
    public void weakReferenceCleanTest() throws InterruptedException {
        ReferenceQueue<Cleanable> referenceQueue = new ReferenceQueue<>();
        AutoCleanTimer.setWeakReferenceQueue(referenceQueue);
        AutoCleanTimer.add(new HashSingleCacheStore<>());
        System.gc();
        Assert.assertNotNull(referenceQueue.remove(100L));
        System.gc();
        Thread.sleep(300L);
        Assert.assertEquals(0, AutoCleanTimer.size());
    }

    @Test
    public void methodExceptionThrowTest() throws InterruptedException {
        class ThrowExceptionCleanable implements Cleanable {
            private boolean throed;
            @Override
            public long clean() throws Exception {
                if (!throed) {
                    throed = true;
                    throw new Exception();
                }
                return 0;
            }
        }

        ThrowExceptionCleanable cleanable = new ThrowExceptionCleanable();
        AutoCleanTimer.add(cleanable);
        Thread.sleep(300L);
        AutoCleanTimer.remove(cleanable);
    }

}