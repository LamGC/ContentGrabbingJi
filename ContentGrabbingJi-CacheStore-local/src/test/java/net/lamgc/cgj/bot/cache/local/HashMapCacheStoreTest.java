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


import net.lamgc.cgj.bot.cache.MapCacheStore;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @see HashMapCacheStore
 */
public class HashMapCacheStoreTest {

    @Test
    public void nullThrowTest() {
        final MapCacheStore<String> cacheStore = new HashMapCacheStore<>();

        Assert.assertThrows(NullPointerException.class, () -> cacheStore.mapSize(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.mapFieldSet(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.mapValueSet(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.put(null, "field", "value"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.put("testKey", null, "value"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.put("testKey", "field", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putAll(null, new HashMap<>()));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putAll("testKey", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putIfNotExist(null, "field", "value"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putIfNotExist("testKey", null, "value"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.putIfNotExist("testKey", "field", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.get("testKey", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.get(null, "field"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.removeField("testKey", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.removeField(null, "field"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.mapIsEmpty(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.clearMap(null));
    }

    @Test
    public void keyNotExistTest() {
        final MapCacheStore<String> cacheStore = new HashMapCacheStore<>();
        final String key = "testKey";

        Assert.assertEquals(-1, cacheStore.mapSize(key));
        Assert.assertFalse(cacheStore.mapIsEmpty(key));
        Assert.assertFalse(cacheStore.clearMap(key));
        Assert.assertFalse(cacheStore.containsField(key, "Field"));
        Assert.assertFalse(cacheStore.removeField(key, "Field"));
        Assert.assertNull(cacheStore.get(key, "Field"));
        Assert.assertTrue(cacheStore.put(key, "Field", "value"));
        Assert.assertTrue("clearMap operation failed!", cacheStore.remove(key));
        Assert.assertTrue(cacheStore.putAll(key, new HashMap<>()));
        Assert.assertTrue("clearMap operation failed!", cacheStore.remove(key));
        Assert.assertTrue(cacheStore.putIfNotExist(key, "Field", "value"));
    }

    @Test
    public void putAndGetTest() {
        final MapCacheStore<String> cacheStore = new HashMapCacheStore<>();
        final String key = "testKey";
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
        final MapCacheStore<String> cacheStore = new HashMapCacheStore<>();
        final String key = "testKey";
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
        Assert.assertTrue(cacheStore.mapIsEmpty(key));

        // removeField 多分支测试
        Assert.assertTrue("put operation failed!", cacheStore.put(key, "test00", "testValue00"));
        Assert.assertTrue(cacheStore.containsField(key, "test00"));
        Assert.assertEquals("testValue00", cacheStore.get(key, "test00"));
        Assert.assertTrue("removeField operation failed!", cacheStore.removeField(key, "test00"));
        Assert.assertFalse(cacheStore.removeField(key, "test00"));
    }


}