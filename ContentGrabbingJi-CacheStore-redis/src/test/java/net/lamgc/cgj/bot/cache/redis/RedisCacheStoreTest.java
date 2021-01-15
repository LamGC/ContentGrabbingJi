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
import org.junit.*;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;

import static net.lamgc.cgj.bot.cache.redis.util.RedisTestUtils.assertDeleteIfExist;
import static net.lamgc.cgj.bot.cache.redis.util.RedisTestUtils.randomString;

/**
 * @see RedisCacheStore
 */
public class RedisCacheStoreTest {

    private final static String KEY_PREFIX = "test:store";

    private static RedisConnectionPool connectionPool;
    private static RedisCacheStore<String> cacheStore;

    private static Jedis jedis;

    @BeforeClass
    public static void beforeAllTest() {
        jedis = new Jedis();
        connectionPool = new RedisConnectionPool();
        cacheStore = new SimpleRedisCacheStore(connectionPool, KEY_PREFIX);

    }

    @AfterClass
    public static void afterAllTest() {
        if (jedis != null && jedis.isConnected()) {
            jedis.close();
        }

        connectionPool.close();
    }

    @Before
    public void beforeTest() {
        clearAllKey();
    }

    @After
    public void afterTest() {
        clearAllKey();
    }

    private void clearAllKey() {
        Set<String> keys = jedis.keys(RedisUtils.toRedisCacheKey(KEY_PREFIX, RedisUtils.CACHE_KEY_ALL));
        if (keys.isEmpty()) {
            return;
        }
        String[] keyArray = new String[keys.size()];
        keys.toArray(keyArray);
        Assert.assertEquals("Failed to delete test key.",
                keyArray.length, jedis.del(keyArray).intValue());
    }

    @Test
    public void setTimeToLiveTest() {
        final CacheKey key = new CacheKey("test_set_time_to_live");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        final long timeToLive = 500;

        assertDeleteIfExist(jedis, keyString);
        Assert.assertFalse("TTL set successfully for nonexistent key.", cacheStore.setTimeToLive(key, timeToLive));
        Assert.assertTrue("Failed to set key value for test.", RedisUtils.isOk(jedis.set(keyString, "value")));

        Assert.assertTrue("Failed to set TTL on key value.", cacheStore.setTimeToLive(key, timeToLive));
        long currentTTL = jedis.ttl(keyString);
        Assert.assertTrue("TTL was not set successfully.", currentTTL >= 0);
        Assert.assertTrue("The actual set TTL is greater than expected.",
                currentTTL < timeToLive);

        Assert.assertTrue("Failed to clear TTL on key value.", cacheStore.setTimeToLive(key, -1));
        currentTTL = jedis.ttl(keyString);
        Assert.assertTrue("TTL was not clear successfully.", currentTTL < 0);
    }

    @Test
    public void getTimeToLiveTest() {
        final CacheKey key = new CacheKey("test_get_time_to_live");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);
        final long timeToLive = 500;

        assertDeleteIfExist(jedis, keyString);
        Assert.assertEquals("The getTimeToLive() method returned a positive integer for a nonexistent key.",
                -1, cacheStore.getTimeToLive(key));
        Assert.assertTrue("Failed to set key value for test.", RedisUtils.isOk(jedis.set(keyString, "value")));

        Assert.assertEquals("Failed to set TTL on key value.", 1, jedis.pexpire(keyString, timeToLive).intValue());
        long currentTTL = cacheStore.getTimeToLive(key);
        Assert.assertTrue("TTL failed to get.", currentTTL > 0);
        Assert.assertTrue("The actual set TTL is greater than expected.",
                currentTTL <= timeToLive);
        Assert.assertTrue("The actual set TTL is greater than expected.",
                currentTTL >= (timeToLive - 5));

        Assert.assertEquals("Failed to clear TTL on key value.",
                1, jedis.persist(keyString).intValue());
        currentTTL = cacheStore.getTimeToLive(key);
        Assert.assertTrue("TTL was not clear successfully.", currentTTL < 0);
    }

    @Test
    public void sizeTest() {
        final String keyPrefix =
                RedisUtils.toRedisCacheKey(KEY_PREFIX, new CacheKey("test_size")) + ':';
        final String allKey = RedisUtils.toRedisCacheKey(keyPrefix, RedisUtils.CACHE_KEY_ALL);

        for (int i = 0; i < 50; i++) {
            Assert.assertTrue("Failed to add key.", RedisUtils.isOk(
                    jedis.set(
                            RedisUtils.toRedisCacheKey(keyPrefix, new CacheKey(randomString(8))),
                            randomString(10)
                    )));

            Assert.assertEquals("Inconsistent number of keys.", jedis.keys(allKey).size(), cacheStore.size());
        }
    }

    @Test
    public void existsTest() {
        final CacheKey key = new CacheKey("test_exists");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);

        assertDeleteIfExist(jedis, keyString);

        Assert.assertFalse("The exists() method returned 'true' for a nonexistent key.", cacheStore.exists(key));
        Assert.assertTrue("Failed to set key value for test.", RedisUtils.isOk(jedis.set(keyString, "value")));
        Assert.assertTrue("The exists() method returned 'false' for the existing key.", cacheStore.exists(key));
    }

    @Test
    public void removeTest() {
        final CacheKey key = new CacheKey("test_remove");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);

        assertDeleteIfExist(jedis, keyString);

        Assert.assertFalse("Attempt to delete nonexistent key succeeded.", cacheStore.remove(key));
        Assert.assertTrue("Failed to set key value for test.", RedisUtils.isOk(jedis.set(keyString, "value")));
        Assert.assertTrue("Key deletion failed.", cacheStore.remove(key));
    }

    @Test
    public void clearTest() {
        final CacheKey key = new CacheKey("test_clear");
        final String keyString = RedisUtils.toRedisCacheKey(KEY_PREFIX, key);

        final String externalKey = "test:external";

        Assert.assertTrue("Failed to set external key.", RedisUtils.isOk(jedis.set(externalKey, "externalValue")));
        Assert.assertTrue("Failed to set test key.", RedisUtils.isOk(jedis.set(keyString, "testValue")));
        Assert.assertNotEquals("The cache store is empty.",
                0, jedis.keys(RedisUtils.toRedisCacheKey(KEY_PREFIX, RedisUtils.CACHE_KEY_ALL)).size());

        Assert.assertTrue(cacheStore.clear());

        Assert.assertEquals("The cache store is not empty.",
                0, jedis.keys(RedisUtils.toRedisCacheKey(KEY_PREFIX, RedisUtils.CACHE_KEY_ALL)).size());
        Assert.assertTrue("External key removed.", jedis.exists(externalKey));
    }

    @Test
    public void keySetTest() {
        clearAllKey();
        Set<String> actualKeys = cacheStore.keySet();
        Assert.assertNotNull("keySet() returned 'null'.", actualKeys);
        Assert.assertEquals("CacheStore is empty, but keySet() is not empty.", 0, actualKeys.size());

        Set<String> expectKeys = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            String nextKey = randomString(10);
            expectKeys.add(nextKey);
            Assert.assertTrue("Failed to set key value for test.",
                    RedisUtils.isOk(jedis.set(KEY_PREFIX + RedisUtils.KEY_SEPARATOR + nextKey, nextKey)));
        }

        actualKeys = cacheStore.keySet();
        Assert.assertNotNull("keySet() returned 'null'.", actualKeys);
        Assert.assertEquals("Number of keys not as expected.", expectKeys.size(), actualKeys.size());

        for (String actualKey : actualKeys) {
            Assert.assertTrue(String.format("Key '%s' does not belong to the expected key.", actualKey),
                    expectKeys.contains(actualKey));
        }
    }

}
