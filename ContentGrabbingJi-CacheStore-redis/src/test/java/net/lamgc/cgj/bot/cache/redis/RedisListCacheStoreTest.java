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
import net.lamgc.cgj.bot.cache.ListCacheStore;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.convert.StringToStringConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @see RedisListCacheStore
 */
public class RedisListCacheStoreTest {

    private final Jedis jedis = new Jedis();
    private final static StringConverter<String> CONVERTER = new StringToStringConverter();
    private final static String IDENTIFY = "test:list";
    private static RedisConnectionPool connectionPool;

    @BeforeClass
    public static void beforeAllTest() {
        connectionPool = new RedisConnectionPool();
        connectionPool.reconnectRedis();
        Assert.assertTrue("Redis is not connected.", connectionPool.available());
    }

    private ListCacheStore<String> newListCacheStore() {
        return new RedisListCacheStore<>(connectionPool, IDENTIFY, CONVERTER);
    }

    private Set<String> getListElements(String key) {
        Set<String> actualElements = new HashSet<>();
        for (long i = 0; i < jedis.llen(key); i++) {
            actualElements.add(jedis.lindex(key, i));
        }
        return actualElements;
    }

    @Before
    public void beforeTest() {
        Set<String> keys = jedis.keys(RedisUtils.toRedisCacheKey(IDENTIFY, RedisUtils.CACHE_KEY_ALL));
        for (String key : keys) {
            jedis.del(key);
        }
    }

    @Test
    public void prefixCheck() throws NoSuchFieldException, IllegalAccessException {
        final Field prefixField = RedisListCacheStore.class.getDeclaredField("keyPrefix");
        prefixField.setAccessible(true);
        String prefix = (String) prefixField.get(new RedisListCacheStore<>(connectionPool, IDENTIFY, CONVERTER));
        Assert.assertTrue(prefix.endsWith(RedisUtils.KEY_SEPARATOR));
        prefix = (String) prefixField.get(new RedisListCacheStore<>(connectionPool,
                IDENTIFY + RedisUtils.KEY_SEPARATOR, CONVERTER));
        Assert.assertTrue(prefix.endsWith(RedisUtils.KEY_SEPARATOR));
        prefixField.setAccessible(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyPrefixTest() {
        new RedisListCacheStore<>(connectionPool, "", CONVERTER);
    }

    @Test(expected = NullPointerException.class)
    public void nullPrefixTest() {
        new RedisListCacheStore<>(connectionPool, null, CONVERTER);
    }

    @Test
    public void addElementTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_add_element");
        final String element = "test";
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        Assert.assertTrue(listCacheStore.addElement(listKey, element));

        Assert.assertEquals(1, jedis.llen(listKeyStr).intValue());
        Assert.assertEquals(element, jedis.lpop(listKeyStr));
    }

    @Test
    public void addElementsTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_add_elements");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        final Set<String> expectedElements = new HashSet<>();
        expectedElements.add("a");
        expectedElements.add("b");
        expectedElements.add("c");
        expectedElements.add("d");
        expectedElements.add("e");

        Assert.assertEquals("The key does not exist, but the empty collection was added successfully.",
                jedis.exists(listKeyStr),
                listCacheStore.addElements(listKey, Collections.emptyList()));


        Assert.assertTrue(listCacheStore.addElements(listKey, expectedElements));

        Assert.assertEquals(expectedElements.size(), jedis.llen(listKeyStr).intValue());
        Set<String> actualElements = getListElements(listKeyStr);
        Assert.assertTrue(actualElements.containsAll(expectedElements));

        Assert.assertEquals("Key does not exist, but adding empty collection failed.",
                jedis.exists(listKeyStr),
                listCacheStore.addElements(listKey, Collections.emptySet()));
    }

    @Test
    public void removeElementByElementTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_remove_element_by_element");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        final List<String> expectedElements = new ArrayList<>();
        expectedElements.add("a");
        expectedElements.add("b");
        expectedElements.add("c");
        expectedElements.add("d");
        expectedElements.add("e");

        final String[] expectedElementsArr = new String[expectedElements.size()];
        expectedElements.toArray(expectedElementsArr);

        jedis.del(listKeyStr);

        Assert.assertFalse(listCacheStore.removeElement(listKey, "NoExistElement"));

        Assert.assertNotEquals("The expected create operation failed.",
                RedisUtils.RETURN_CODE_FAILED, jedis.lpush(listKeyStr, expectedElementsArr).intValue());

        Random random = new Random();
        final int deletedIndex = random.nextInt(expectedElements.size());
        Assert.assertTrue("The operation to be tested failed.",
                listCacheStore.removeElement(listKey, expectedElements.get(deletedIndex)));

        expectedElements.remove(deletedIndex);

        Assert.assertEquals(expectedElements.size(), jedis.llen(listKeyStr).intValue());
        Set<String> actualElements = getListElements(listKeyStr);
        Assert.assertTrue(actualElements.containsAll(expectedElements));
    }

    @Test
    public void removeElementByIndexTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_remove_element_by_index");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        final Set<String> expectedElements = new HashSet<>();
        expectedElements.add("a");
        expectedElements.add("b");
        expectedElements.add("c");
        expectedElements.add("d");
        expectedElements.add("e");

        final String[] expectedElementsArr = new String[expectedElements.size()];
        expectedElements.toArray(expectedElementsArr);

        jedis.del(listKeyStr);
        // 尝试删除不存在的 Key
        Assert.assertFalse(listCacheStore.removeElement(listKey, 0));
        Assert.assertNotEquals("The expected create operation failed.",
                RedisUtils.RETURN_CODE_FAILED, jedis.lpush(listKeyStr, expectedElementsArr).intValue());
        Assert.assertFalse(
                listCacheStore.removeElement(listKey, jedis.llen(listKeyStr).intValue()));

        Random random = new Random();
        final int deletedIndex = random.nextInt(expectedElements.size());
        String deletedElement = jedis.lindex(listKeyStr, deletedIndex);

        Assert.assertTrue("The operation to be tested failed.",
                listCacheStore.removeElement(listKey, deletedIndex));

        expectedElements.remove(deletedElement);

        Assert.assertEquals(expectedElements.size(), jedis.llen(listKeyStr).intValue());
        Set<String> actualElements = getListElements(listKeyStr);
        Assert.assertTrue(actualElements.containsAll(expectedElements));
    }

    @Test
    public void containsElementTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_contains_element");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        final Set<String> expectedElements = new HashSet<>();
        expectedElements.add("a");
        expectedElements.add("b");
        expectedElements.add("c");
        expectedElements.add("d");
        expectedElements.add("e");

        final String[] expectedElementsArr = new String[expectedElements.size()];
        expectedElements.toArray(expectedElementsArr);

        Assert.assertNotEquals(-1, jedis.lpush(listKeyStr, expectedElementsArr).intValue());

        Set<String> actualElements = getListElements(listKeyStr);
        expectedElements.add("f");
        expectedElements.add("g");
        expectedElements.add("h");
        expectedElements.add("i");

        for (String expectedElement : expectedElements) {
            Assert.assertEquals(String.format("Make a difference: '%s'", expectedElement),
                    actualElements.contains(expectedElement),
                    listCacheStore.containsElement(listKey, expectedElement));
        }
    }

    @Test
    public void isEmptyTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_is_empty");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);

        Assert.assertEquals(!jedis.exists(listKeyStr), listCacheStore.isEmpty(listKey));
        jedis.lpush(listKeyStr, "test");
        Assert.assertEquals(jedis.exists(listKeyStr), !listCacheStore.isEmpty(listKey));
    }

    @Test
    public void elementsLengthTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_elements_length");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        final Set<String> expectedElements = new HashSet<>();
        expectedElements.add("a");
        expectedElements.add("b");
        expectedElements.add("c");
        expectedElements.add("d");
        expectedElements.add("e");
        final String[] expectedElementsArr = new String[expectedElements.size()];
        expectedElements.toArray(expectedElementsArr);

        long beforeLength = jedis.llen(listKeyStr);
        if (jedis.llen(listKeyStr) == 0) {
            Assert.assertEquals(-1, listCacheStore.elementsLength(listKey));
        } else {
            Assert.assertEquals(beforeLength, listCacheStore.elementsLength(listKey));
        }

        jedis.del(listKeyStr);
        jedis.lpush(listKeyStr, expectedElementsArr);

        Assert.assertEquals(jedis.llen(listKeyStr).intValue(), listCacheStore.elementsLength(listKey));
    }

    @Test
    public void clearCollectionTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_clear_collection");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        final Set<String> expectedElements = new HashSet<>();
        expectedElements.add("a");
        expectedElements.add("b");
        expectedElements.add("c");
        expectedElements.add("d");
        expectedElements.add("e");
        final String[] expectedElementsArr = new String[expectedElements.size()];
        expectedElements.toArray(expectedElementsArr);

        Assert.assertEquals(jedis.exists(listKeyStr), listCacheStore.clearCollection(listKey));

        jedis.lpush(listKeyStr, expectedElementsArr);

        Assert.assertTrue(listCacheStore.clearCollection(listKey));
        Assert.assertEquals(0, jedis.llen(listKeyStr).intValue());
        Assert.assertFalse(jedis.exists(listKeyStr));
    }

    @Test
    public void getElementTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_get_element");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        final List<String> expectedElements = new ArrayList<>();
        expectedElements.add("a");
        expectedElements.add("b");
        expectedElements.add("c");
        expectedElements.add("d");
        expectedElements.add("e");
        final String[] expectedElementsArr = new String[expectedElements.size()];
        expectedElements.toArray(expectedElementsArr);
        jedis.lpush(listKeyStr, expectedElementsArr);

        Collections.reverse(expectedElements);
        for (int i = 0; i < expectedElements.size(); i++) {
            Assert.assertEquals("index: " + i, expectedElements.get(i),
                    listCacheStore.getElement(listKey, i));
        }
    }

    @Test
    public void getElementsByRangeTest() {
        ListCacheStore<String> listCacheStore = newListCacheStore();
        final CacheKey listKey = new CacheKey("list_get_elements_by_range");
        final String listKeyStr = RedisUtils.toRedisCacheKey(IDENTIFY, listKey);
        final List<String> expectedElements = new ArrayList<>();
        expectedElements.add("a");
        expectedElements.add("b");
        expectedElements.add("c");
        expectedElements.add("d");
        expectedElements.add("e");
        expectedElements.add("f");
        expectedElements.add("g");
        expectedElements.add("h");
        expectedElements.add("i");
        expectedElements.add("j");
        expectedElements.add("k");
        final String[] expectedElementsArr = new String[expectedElements.size()];
        expectedElements.toArray(expectedElementsArr);
        jedis.lpush(listKeyStr, expectedElementsArr);

        Collections.reverse(expectedElements);

        final int start = 2;
        final int length = 4;

        List<String> actualElements = listCacheStore.getElementsByRange(listKey, start, length);

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(expectedElements.get(start + i), actualElements.get(i));
        }

    }


}