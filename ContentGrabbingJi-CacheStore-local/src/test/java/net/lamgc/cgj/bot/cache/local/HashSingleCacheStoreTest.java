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

import net.lamgc.cgj.bot.cache.SingleCacheStore;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @see HashSingleCacheStore
 * @see HashCacheStore
 */
public class HashSingleCacheStoreTest {

    @Test
    public void nullThrowTest() {
        final SingleCacheStore<String> cacheStore = new HashSingleCacheStore<>();

        // HashSingleCacheStore
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.set(null, "testValue"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.set("testKey", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.get(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.setIfNotExist(null, "testValue"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.setIfNotExist("testKey", null));

        // HashCacheStore
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.exists(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.getTimeToLive(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.setTimeToLive(null, 0));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.remove(null));
    }

    @Test
    public void setAndGetTest() {
        SingleCacheStore<String> cacheStore = new HashSingleCacheStore<>();
        final String key = "test01";
        final String value = "testValue";

        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));
        Assert.assertEquals(value, cacheStore.get("test01"));
        Assert.assertTrue("Remove operation failed!", cacheStore.remove(key));
        Assert.assertNull("Set operation failed!", cacheStore.get(key));
    }

    @Test
    public void setIfNotExistTest() {
        SingleCacheStore<String> cacheStore = new HashSingleCacheStore<>();
        final String key = "test01";
        final String value = "testValue";
        final String value2 = "testValue02";
        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));

        Assert.assertFalse(cacheStore.setIfNotExist(key, value2));
        Assert.assertEquals(value, cacheStore.get(key));
    }

    @Test
    public void expireTest() throws InterruptedException {
        final SingleCacheStore<String> cacheStore = new HashSingleCacheStore<>();
        final String key = "test01";
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
        final SingleCacheStore<String> cacheStore = new HashSingleCacheStore<>();
        final String key = "test01";
        final String value = "testValue";

        // 删除不存在Cache测试
        Assert.assertFalse(cacheStore.remove(key));
        // 删除存在的Cache测试
        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));
        Assert.assertTrue(cacheStore.remove(key));
    }

    @Test
    public void clearTest() {
        final SingleCacheStore<String> cacheStore = new HashSingleCacheStore<>();
        final String key = "test01";
        final String value = "testValue";

        Assert.assertTrue("Set operation failed!", cacheStore.set(key, value));

        Assert.assertTrue(cacheStore.exists(key));
        Assert.assertTrue("Clear operation failed!", cacheStore.clear());
        Assert.assertFalse(cacheStore.exists(key));
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

        final SingleCacheStore<String> cacheStore = new HashSingleCacheStore<>();
        expectedMap.forEach(cacheStore::set);
        Assert.assertEquals(expectedMap.size(), cacheStore.size());
        Assert.assertTrue(expectedMap.keySet().containsAll(cacheStore.keySet()));
    }

}
