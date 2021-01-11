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

package net.lamgc.cgj.bot.cache.redis;

import com.google.common.base.Throwables;
import net.lamgc.cgj.bot.cache.CacheKey;
import net.lamgc.cgj.bot.cache.SingleCacheStore;
import net.lamgc.cgj.bot.cache.convert.StringToStringConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @see RedisCacheStore
 * @see RedisSingleCacheStore
 */
public class RedisSingleCacheStoreTest {

    private final static RedisCacheStoreFactory factory;
    private final static TemporaryFolder tempFolder = TemporaryFolder.builder().build();

    static {
        try {
            tempFolder.create();
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
        factory = new RedisCacheStoreFactory();
        try {
            factory.initial(tempFolder.newFolder("cache-redis"));
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
    }

    private final static SingleCacheStore<String> cacheStore = factory.newSingleCacheStore("test:single", new StringToStringConverter());

    @Before
    public void before() {
        Assert.assertTrue(cacheStore.clear());
    }

    @Test
    public void nullThrowTest() {
        final SingleCacheStore<String> tempCacheStore = factory.newSingleCacheStore("test:single" + RedisUtils.KEY_SEPARATOR, new StringToStringConverter());
        final CacheKey key = new CacheKey("testKey");

        // RedisSingleCacheStore
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.set(null, "testValue"));
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.set(key, null));
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.get(null));
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.setIfNotExist(null, "testValue"));
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.setIfNotExist(key, null));

        // RedisCacheStore
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.exists(null));
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.getTimeToLive(null));
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.setTimeToLive(null, 0));
        Assert.assertThrows(NullPointerException.class, () -> tempCacheStore.remove(null));
    }

    @Test
    public void setAndGetTest() {
        final CacheKey key = new CacheKey("testKey");
        final String value = "testValue";

        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));
        Assert.assertEquals(value, cacheStore.get(key));
        Assert.assertTrue("Remove operation failed!", cacheStore.remove(key));
        Assert.assertNull("Set operation failed!", cacheStore.get(key));
    }

    @Test
    public void setIfNotExistTest() {
        final CacheKey key = new CacheKey("testKey");
        final String value = "testValue";
        final String value2 = "testValue02";
        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));

        Assert.assertFalse(cacheStore.setIfNotExist(key, value2));
        Assert.assertEquals(value, cacheStore.get(key));
    }

    @Test
    public void expireTest() throws InterruptedException {
        final CacheKey key = new CacheKey("testKey");
        final String value = "testValue";

        // Cache
        Assert.assertFalse(cacheStore.setTimeToLive(key, 300));
        Assert.assertEquals(-1, cacheStore.getTimeToLive(key));

        // TTL 到期被动检查测试: 使用 exists 经 expire 检查失败后返回 false.
        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));
        Assert.assertTrue("SetTTL operation failed!", cacheStore.setTimeToLive(key, 200));
        Assert.assertNotEquals(-1, cacheStore.getTimeToLive(key));
        Thread.sleep(300);
        Assert.assertFalse(cacheStore.exists(key));

        // 取消 TTL 测试
        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));
        Assert.assertTrue("SetTTL operation failed!", cacheStore.setTimeToLive(key, 200));
        Assert.assertTrue("SetTTL operation failed!", cacheStore.setTimeToLive(key, -1));
        Thread.sleep(300);
        Assert.assertTrue(cacheStore.exists(key));
        Assert.assertEquals(-1, cacheStore.getTimeToLive(key));
    }

    @Test
    public void removeTest() {
        final CacheKey key = new CacheKey("testKey");
        final String value = "testValue";

        // 删除不存在Cache测试
        Assert.assertFalse(cacheStore.remove(key));
        // 删除存在的Cache测试
        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));
        Assert.assertTrue(cacheStore.remove(key));
    }

    @Test
    public void clearTest() {
        final SingleCacheStore<String> secondSingleCacheStore =
                factory.newSingleCacheStore("test:single_b", new StringToStringConverter());
        final CacheKey key = new CacheKey("testKey");
        final String value = "testValue";

        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));
        Assert.assertTrue("Set operation failed!", secondSingleCacheStore.set(key, value));

        Assert.assertTrue(cacheStore.exists(key));
        Assert.assertTrue("Clear operation failed!", cacheStore.clear());
        Assert.assertFalse(cacheStore.exists(key));
        Assert.assertTrue(secondSingleCacheStore.exists(key));
    }

    @Test
    public void sizeAndKeySetTest() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("test01", "testValue01");
        expectedMap.put("test02", "testValue02");
        expectedMap.put("test03", "testValue03");
        expectedMap.put("test04", "testValue04");
        expectedMap.put("test05", "testValue05");
        expectedMap.put("test06", "testValue06");

        Assert.assertEquals(0, cacheStore.size());
        expectedMap.forEach((key, value) -> cacheStore.set(new CacheKey(key), value));
        Assert.assertEquals(expectedMap.size(), cacheStore.size());
        Assert.assertTrue(expectedMap.keySet().containsAll(cacheStore.keySet()));
        Assert.assertTrue(cacheStore.keySet().containsAll(expectedMap.keySet()));
    }
    
}
