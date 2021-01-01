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
import net.lamgc.cgj.bot.cache.MapCacheStore;
import net.lamgc.cgj.bot.cache.convert.StringToStringConverter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @see RedisMapCacheStore
 */
public class RedisMapCacheStoreTest {

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

    private final static MapCacheStore<String> cacheStore = factory.newMapCacheStore("test", new StringToStringConverter());

    @Before
    public void before() {
        Assert.assertTrue(cacheStore.clear());
    }

    @After
    public void after() {
        Assert.assertTrue(cacheStore.clear());
    }

    @Test
    public void nullThrowTest() {
        final CacheKey key = new CacheKey("testKey");
        
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.mapSize(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.mapFieldSet(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.mapValueSet(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.put(null, "field", "value"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.put(key, null, "value"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.put(key, "field", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putAll(null, new HashMap<>()));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putAll(key, null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putIfNotExist(null, "field", "value"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putIfNotExist(key, null, "value"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putIfNotExist(key, "field", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.get(key, null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.get(null, "field"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.removeField(key, null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.removeField(null, "field"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.mapIsEmpty(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.clearMap(null));
    }

    @Test
    public void keyNotExistTest() {
        final CacheKey key = new CacheKey("testKey");
        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("testField", "value");

        // 例外情况: 因为 Redis 对空 Map 的处理机制, 导致返回只能为空.
        Assert.assertEquals(0, cacheStore.mapSize(key));
        // 例外情况: mapIsEmpty 在 Redis 没有相应指令, 所以是依靠 mapSize 实现的,
        //     同样因 mapSize 的原因, 不存在等于空.
        Assert.assertTrue(cacheStore.mapIsEmpty(key));

        Assert.assertFalse(cacheStore.clearMap(key));
        Assert.assertFalse(cacheStore.containsField(key, "Field"));
        Assert.assertFalse(cacheStore.removeField(key, "Field"));
        Assert.assertNull(cacheStore.get(key, "Field"));
        Assert.assertTrue(cacheStore.put(key, "Field", "value"));
        Assert.assertTrue("clearMap operation failed!", cacheStore.remove(key));
        Assert.assertTrue(cacheStore.putAll(key, testMap));
        Assert.assertTrue("clearMap operation failed!", cacheStore.remove(key));
        Assert.assertTrue(cacheStore.putIfNotExist(key, "Field", "value"));
    }

    @Test
    public void putAndGetTest() {
        final CacheKey key = new CacheKey("testKey");
        final Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("test01", "testValue01");
        expectedMap.put("test02", "testValue02");
        expectedMap.put("test03", "testValue03");
        expectedMap.put("test04", "testValue04");
        expectedMap.put("test05", "testValue05");
        expectedMap.put("test06", "testValue06");

        // put/get, mapIsEmpty, containsField
        Assert.assertTrue("put operation failed!", cacheStore.put(key, "test00", "testValue00"));
        Assert.assertTrue(cacheStore.containsField(key, "test00"));
        Assert.assertEquals("testValue00", cacheStore.get(key, "test00"));
        Assert.assertTrue("removeField operation failed!", cacheStore.removeField(key, "test00"));

        // putIfNotExist
        Assert.assertTrue(cacheStore.putIfNotExist(key, "test00", "testValue00"));
        Assert.assertFalse(cacheStore.putIfNotExist(key, "test00", "testValue00"));
        Assert.assertTrue("clearMap operation failed!", cacheStore.clearMap(key));

        // putAll
        Assert.assertTrue(cacheStore.putAll(key, expectedMap));
        Assert.assertTrue(expectedMap.keySet().containsAll(cacheStore.mapFieldSet(key)));
        Assert.assertTrue(expectedMap.values().containsAll(cacheStore.mapValueSet(key)));
    }

    @Test
    public void fieldChangeTest() {
        final CacheKey key = new CacheKey("testKey");
        final Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("test01", "testValue01");
        expectedMap.put("test02", "testValue02");
        expectedMap.put("test03", "testValue03");
        expectedMap.put("test04", "testValue04");
        expectedMap.put("test05", "testValue05");
        expectedMap.put("test06", "testValue06");

        // mapSize, clearMap, mapIsEmpty 测试
        Assert.assertTrue("putAll operation failed!", cacheStore.putAll(key, expectedMap));
        Assert.assertEquals(expectedMap.size(), cacheStore.mapSize(key));
        Assert.assertTrue(cacheStore.clearMap(key));
        Assert.assertEquals(0, cacheStore.mapSize(key));
        Assert.assertTrue(cacheStore.mapIsEmpty(key));

        // removeField 多分支测试
        Assert.assertTrue("put operation failed!", cacheStore.put(key, "test00", "testValue00"));
        Assert.assertTrue(cacheStore.containsField(key, "test00"));
        Assert.assertEquals("testValue00", cacheStore.get(key, "test00"));
        Assert.assertTrue("removeField operation failed!", cacheStore.removeField(key, "test00"));
        Assert.assertFalse(cacheStore.removeField(key, "test00"));
    }


}