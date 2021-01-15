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

import net.lamgc.cgj.bot.cache.CacheKey;
import net.lamgc.cgj.bot.cache.SingleCacheStore;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.convert.StringToStringConverter;
import org.junit.*;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;

import static net.lamgc.cgj.bot.cache.redis.util.RedisTestUtils.*;

/**
 * @see RedisCacheStore
 * @see RedisSingleCacheStore
 */
public class RedisSingleCacheStoreTest {

    private final static String KEY_PREFIX = "test:single";
    private final static StringConverter<String> CONVERTER = new StringToStringConverter();

    private static Jedis jedis;
    private final RedisConnectionPool connectionPool = new RedisConnectionPool();
    private final SingleCacheStore<String> cacheStore =
            new RedisSingleCacheStore<>(connectionPool, KEY_PREFIX, CONVERTER);

    @BeforeClass
    public static void beforeAllTest() {
        jedis = new Jedis();
    }

    @AfterClass
    public static void afterAllTest() {
        if (jedis != null && jedis.isConnected()) {
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
        new RedisSingleCacheStore<>(connectionPool, "", CONVERTER);
    }

    @Test(expected = NullPointerException.class)
    public void nullPrefixTest() {
        new RedisSingleCacheStore<>(connectionPool, null, CONVERTER);
    }

    @Test
    public void prefixCheck() throws NoSuchFieldException, IllegalAccessException {
        final Field prefixField = RedisSingleCacheStore.class.getDeclaredField("keyPrefix");
        prefixField.setAccessible(true);
        String prefix = (String) prefixField.get(new RedisSingleCacheStore<>(connectionPool, KEY_PREFIX, CONVERTER));
        Assert.assertTrue("The prefix does not contain a separator at the end.",
                prefix.endsWith(RedisUtils.KEY_SEPARATOR));
        prefix = (String) prefixField.get(new RedisSingleCacheStore<>(connectionPool,
                KEY_PREFIX + RedisUtils.KEY_SEPARATOR, CONVERTER));
        Assert.assertTrue("The separator at the end of the prefix is missing.",
                prefix.endsWith(RedisUtils.KEY_SEPARATOR));
        prefixField.setAccessible(false);
    }


    @Test
    public void setTest() {
        final CacheKey key = new CacheKey("test_set");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        String value = randomString(10);
        assertDeleteIfExist(jedis, keyString);

        Assert.assertTrue("Failed to set value on specified key.", cacheStore.set(key, value));
        Assert.assertEquals("The value set does not match the expected value.",
                value, jedis.get(keyString));

        value = randomString(12);
        Assert.assertTrue("Cannot rewritten the value of an existing key.", cacheStore.set(key, value));
        Assert.assertEquals("The rewritten value does not match the expected value.",
                value, jedis.get(keyString));
    }

    @Test
    public void setIfNotExistTest() {
        final CacheKey key = new CacheKey("test_set_if_not_exist");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        String value = randomString(10);
        assertDeleteIfExist(jedis, keyString);

        Assert.assertTrue("Failed to set value on specified key.", cacheStore.setIfNotExist(key, value));
        Assert.assertEquals("The value set does not match the expected value.",
                value, jedis.get(keyString));

        value = randomString(12);
        Assert.assertFalse("Write value to existing key succeeded.", cacheStore.setIfNotExist(key, value));
        Assert.assertNotEquals("The key value is modified and the method setIfNotExist() returns 'false'.",
                value, jedis.get(keyString));
    }

    @Test
    public void getTest() {
        final CacheKey key = new CacheKey("test_get");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        String value = randomString(10);
        assertDeleteIfExist(jedis, keyString);

        Assert.assertNull("The get() method returned a non null value for a nonexistent key.",
                cacheStore.get(key));

        Assert.assertTrue("Failed to set test key.", RedisUtils.isOk(jedis.set(keyString, value)));

        Assert.assertEquals("The value obtained does not match the expected value.",
                value, cacheStore.get(key));
    }

}
