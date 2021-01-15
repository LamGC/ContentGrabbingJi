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
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.convert.StringToStringConverter;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.lamgc.cgj.bot.cache.redis.util.RedisTestUtils.assertDeleteIfExist;
import static net.lamgc.cgj.bot.cache.redis.util.RedisTestUtils.randomString;

/**
 * @see RedisMapCacheStore
 */
public class RedisMapCacheStoreTest {

    private final static TemporaryFolder tempFolder = TemporaryFolder.builder().build();
    private final static String KEY_PREFIX = "test:map";
    private final static StringConverter<String> CONVERTER = new StringToStringConverter();
    private final static RedisCacheStoreFactory factory = new RedisCacheStoreFactory();

    private static MapCacheStore<String> cacheStore;

    private static Jedis jedis;

    @BeforeClass
    public static void beforeAll() {
        jedis = new Jedis();
        try {
            tempFolder.create();
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
            return;
        }

        try {
            factory.initial(tempFolder.newFolder("Redis"));
            cacheStore =
                    factory.newMapCacheStore(KEY_PREFIX, CONVERTER);
        } catch (IOException e) {
            Assert.fail(Throwables.getStackTraceAsString(e));
        }
    }

    @AfterClass
    public static void afterAll() {
        if (jedis != null) {
            jedis.close();
        }
    }

    @Before
    public void before() {
        Assert.assertTrue("Clear execution failed before the test started.", cacheStore.clear());
    }

    @After
    public void after() {
        Assert.assertTrue("After the test, clear execution failed", cacheStore.clear());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyPrefixTest() {
        factory.newMapCacheStore("", CONVERTER);
    }

    @Test(expected = NullPointerException.class)
    public void nullPrefixTest() {
        factory.newMapCacheStore(null, CONVERTER);
    }
    
    @Test
    public void prefixCheck() throws NoSuchFieldException, IllegalAccessException {
        final Field prefixField = RedisMapCacheStore.class.getDeclaredField("keyPrefix");
        prefixField.setAccessible(true);
        String prefix = (String) prefixField.get(factory.newMapCacheStore(KEY_PREFIX, CONVERTER));
        Assert.assertTrue("The prefix does not contain a separator at the end.",
                prefix.endsWith(RedisUtils.KEY_SEPARATOR));
        prefix = (String) prefixField.get(factory.newMapCacheStore(
                KEY_PREFIX + RedisUtils.KEY_SEPARATOR, CONVERTER));
        Assert.assertTrue("The separator at the end of the prefix is missing.",
                prefix.endsWith(RedisUtils.KEY_SEPARATOR));
        prefixField.setAccessible(false);
    }

    @Test
    public void nullThrowTest() {
        final CacheKey key = new CacheKey("test_null_throw");

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
    public void putTest() {
        final CacheKey key = new CacheKey("test_put");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        final String field = "field01";
        final String value = "value01";

        if (jedis.hexists(keyString, field)) {
            Assert.assertEquals("The field used by the test is occupied and deletion failed.",
                    1, jedis.hdel(keyString, field).intValue());
        }
        Assert.assertTrue("The operation to be tested failed.", cacheStore.put(key, field, value));
        Assert.assertTrue("The field does not exist after put.", jedis.hexists(keyString, field));
        Assert.assertEquals("The value of the field changes after put.", value, jedis.hget(keyString, field));
    }

    @Test
    public void getTest() {
        final CacheKey key = new CacheKey("test_get");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        final String field = "field01";
        final String value = "value01";

        assertDeleteIfExist(jedis, keyString);
        Assert.assertNull("Method returned a non null value for a field that does not exist.",
                cacheStore.get(key, field));

        Assert.assertTrue("Failed to set the field used by the test.",
                jedis.hset(keyString, field, value).intValue() >= 0);
        Assert.assertTrue("The field used by the test does not exist.",
                jedis.hexists(keyString, field));

        Assert.assertEquals("The obtained field value does not match the expected value.",
                value, cacheStore.get(key, field));
    }

    @Test
    public void putAllTest() {
        final CacheKey key = new CacheKey("test_put_all");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);

        final Map<String, String> expectMap = new HashMap<>();
        expectMap.put("field01", "value01");
        expectMap.put("field02", "value02");
        expectMap.put("field03", "value03");
        expectMap.put("field04", "value04");
        expectMap.put("field05", "value05");
        expectMap.put("field06", "value06");
        expectMap.put("field07", "value07");

        assertDeleteIfExist(jedis, keyString);

        Assert.assertEquals("The key does not exist, but the empty collection was added successfully.",
                jedis.exists(keyString),
                cacheStore.putAll(key, Collections.emptyMap()));

        Assert.assertTrue("The operation to be tested failed.", cacheStore.putAll(key, expectMap));

        Map<String, String> actualMap = jedis.hgetAll(keyString);
        for (String actualKey : actualMap.keySet()) {
            Assert.assertTrue("Field does not exist in the expected.", expectMap.containsKey(actualKey));
            Assert.assertEquals("The value of field " + actualKey + " does not match the expected value.",
                    expectMap.get(actualKey), actualMap.get(actualKey));
        }

        Assert.assertEquals("Key does not exist, but adding empty collection failed.",
                jedis.exists(keyString),
                cacheStore.putAll(key, Collections.emptyMap()));
    }

    @Test
    public void putIfNotExistTest() {
        final CacheKey key = new CacheKey("test_put_if_not_exist");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        final String field = "field01";
        final String value = "value01";

        if (jedis.hexists(keyString, field)) {
            Assert.assertEquals("The field used by the test is occupied and deletion failed.",
                    1, jedis.hdel(keyString, field).intValue());
        }

        Assert.assertTrue("Field does not exist but put failed.", cacheStore.putIfNotExist(key, field, value));

        Assert.assertFalse("Field does exist but put successful.", cacheStore.putIfNotExist(key, field, value));
    }

    @Test
    public void mapSizeTest() {
        final CacheKey key = new CacheKey("test_map_size");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);

        final Map<String, String> expectMap = new HashMap<>();
        expectMap.put("field01", "value01");
        expectMap.put("field02", "value02");
        expectMap.put("field03", "value03");
        expectMap.put("field04", "value04");
        expectMap.put("field05", "value05");
        expectMap.put("field06", "value06");
        expectMap.put("field07", "value07");

        assertDeleteIfExist(jedis, keyString);

        Assert.assertEquals("Non 0 returned when map does not exist.", 0, cacheStore.mapSize(key));
        Assert.assertEquals("The number of test fields prepared is inconsistent.",
                expectMap.size(), jedis.hset(keyString, expectMap).intValue());

        Assert.assertEquals("The number of fields obtained does not match the actual number.",
                expectMap.size(), cacheStore.mapSize(key));
    }

    @Test
    public void mapIsEmptyTest() {
        final CacheKey key = new CacheKey("test_put_if_not_exist");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        final String field = "field01";
        final String value = "value01";

        if (jedis.hlen(keyString) != 0) {
            Assert.assertFalse("Map exists but returns 'true'.", cacheStore.mapIsEmpty(key));
            Assert.assertEquals("Failed to delete map.", 1, jedis.del(keyString).intValue());
            Assert.assertTrue("Map not exists but returns 'false'.", cacheStore.mapIsEmpty(key));
        } else {
            Assert.assertTrue("Map not exists but returns 'false'.", cacheStore.mapIsEmpty(key));
            Assert.assertEquals("Failed to set field value for test.",
                    1, jedis.hset(keyString, field, value).intValue());
            Assert.assertFalse("Map exists but returns 'true'.", cacheStore.mapIsEmpty(key));
        }
    }

    @Test
    public void mapFieldSetTest() {
        final CacheKey key = new CacheKey("test_map_field_set");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);

        final Map<String, String> expectMap = new HashMap<>();
        expectMap.put("field01", "value01");
        expectMap.put("field02", "value02");
        expectMap.put("field03", "value03");
        expectMap.put("field04", "value04");
        expectMap.put("field05", "value05");
        expectMap.put("field06", "value06");
        expectMap.put("field07", "value07");

        assertDeleteIfExist(jedis, keyString);

        Assert.assertNull("The mapFieldSet() method returned a non null value for a nonexistent map." +
                "(If the map does not exist, null should be returned instead of an empty set)",
                cacheStore.mapFieldSet(key));

        Assert.assertEquals("The number of test fields prepared is inconsistent.",
                expectMap.size(), jedis.hset(keyString, expectMap).intValue());

        Set<String> fieldSet = cacheStore.mapFieldSet(key);
        Assert.assertNotNull("Method returns 'null' for the existing map.", fieldSet);

        Assert.assertTrue("The actual set is different from the expectation.",
                fieldSet.containsAll(expectMap.keySet()));
        Assert.assertTrue("The actual set is different from the expectation.",
                expectMap.keySet().containsAll(fieldSet));
    }

    @Test
    public void mapValueSetTest() {
        final CacheKey key = new CacheKey("test_map_value_set");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);

        final Map<String, String> expectMap = new HashMap<>();
        expectMap.put("field01", "value01");
        expectMap.put("field02", "value02");
        expectMap.put("field03", "value03");
        expectMap.put("field04", "value04");
        expectMap.put("field05", "value05");
        expectMap.put("field06", "value06");
        expectMap.put("field07", "value07");

        assertDeleteIfExist(jedis, keyString);

        Assert.assertNull("The mapValueSet() method returned a non null value for a nonexistent map." +
                        "(If the map does not exist, null should be returned instead of an empty set)",
                cacheStore.mapValueSet(key));

        Assert.assertEquals("The number of test fields prepared is inconsistent.",
                expectMap.size(), jedis.hset(keyString, expectMap).intValue());

        Set<String> valueSet = cacheStore.mapValueSet(key);
        Assert.assertNotNull("Method returns 'null' for the existing map.", valueSet);

        Assert.assertTrue("The actual set is different from the expectation.",
                valueSet.containsAll(expectMap.values()));
        Assert.assertTrue("The actual set is different from the expectation.",
                expectMap.values().containsAll(valueSet));
    }

    @Test
    public void removeFieldTest() {
        final CacheKey key = new CacheKey("test_remove_field");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        final String field = "field";
        final String value = "value";

        final Map<String, String> expectMap = new HashMap<>();
        expectMap.put("field01", "value01");
        expectMap.put("field02", "value02");
        expectMap.put("field03", "value03");
        expectMap.put("field04", "value04");
        expectMap.put("field05", "value05");
        expectMap.put("field06", "value06");
        expectMap.put("field07", "value07");

        assertDeleteIfExist(jedis, keyString);

        Assert.assertFalse("Returns 'true' when trying to delete a field in a nonexistent map.",
                cacheStore.removeField(key, "field"));

        Assert.assertEquals("Failed to add field for test.",
                1, jedis.hset(keyString, field, value).intValue());
        Assert.assertFalse("Returns 'true' when trying to delete a field that does not exist in the map.",
                cacheStore.removeField(key, randomString(10)));

        assertDeleteIfExist(jedis, keyString);

        Assert.assertEquals("Failed to add field for test.",
                expectMap.size(), jedis.hset(keyString, expectMap).intValue());

        for (String expectField : expectMap.keySet()) {
            Assert.assertTrue("The attempt to delete an existing field failed: '" + expectField + "'.",
                    cacheStore.removeField(key, expectField));
        }
    }

    @Test
    public void containsFieldTest() {
        final CacheKey key = new CacheKey("test_contains_field");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        final String field = "field";
        final String value = "value";

        assertDeleteIfExist(jedis, keyString);

        Assert.assertFalse("Returns 'true' when trying to check for a map that does not exist.",
                cacheStore.containsField(key, "field"));

        Assert.assertEquals("Failed to add field for test.",
                1, jedis.hset(keyString, field, value).intValue());
        Assert.assertFalse("Returns 'true' when trying to check for a field that does not exist.",
                cacheStore.removeField(key, randomString(10)));

        Assert.assertTrue("An attempt to check for existing fields returned 'false'.",
                cacheStore.containsField(key, field));
    }

    @Test
    public void clearMapTest() {
        final CacheKey key = new CacheKey("test_remove_field");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);

        final Map<String, String> expectMap = new HashMap<>();
        expectMap.put("field01", "value01");
        expectMap.put("field02", "value02");
        expectMap.put("field03", "value03");
        expectMap.put("field04", "value04");
        expectMap.put("field05", "value05");
        expectMap.put("field06", "value06");
        expectMap.put("field07", "value07");

        assertDeleteIfExist(jedis, keyString);

        Assert.assertFalse("Attempt to empty nonexistent map succeeded.", cacheStore.clearMap(key));

        Assert.assertEquals("Failed to add field for test.",
                expectMap.size(), jedis.hset(keyString, expectMap).intValue());
        Assert.assertTrue("The operation to be tested failed.", cacheStore.clearMap(key));
        Assert.assertEquals("After the clear operation, the map still contains fields.",
                0, jedis.hlen(keyString).intValue());
    }

}