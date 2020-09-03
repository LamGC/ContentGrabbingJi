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

import com.google.common.collect.Lists;
import net.lamgc.cgj.bot.cache.ListCacheStore;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @see CopyOnWriteArrayListCacheStore
 * @see LocalCollectionCacheStore
 */
public class ListCacheStoreTest {

    @Test
    public void nullThrowTest() {
        final ListCacheStore<String> cacheStore = new CopyOnWriteArrayListCacheStore<>();

        // LocalCollectionCacheStore
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.addElement(null, "testValue"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.addElement("testKey", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.addElements(null, new ArrayList<>()));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.addElements("testKey", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.elementsLength(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.containsElement(null, "testValue"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.containsElement("testKey", null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.isEmpty(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.clearCollection(null));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.removeElement(null, "testValue"));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.removeElement("testKey", null));

        // CopyOnWriteArrayListCacheStore
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.getElement(null, 0));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.getElementsByRange(null, 0, 0));
        Assert.assertThrows(NullPointerException.class, () -> cacheStore.removeElement(null, 0));

    }

    @Test
    public void notExistCacheTest() {
        final ListCacheStore<String> cacheStore = new CopyOnWriteArrayListCacheStore<>();
        final String key = "testKey";
        Assert.assertFalse(cacheStore.clearCollection(key));
        Assert.assertFalse(cacheStore.isEmpty(key));
        Assert.assertEquals(-1, cacheStore.elementsLength(key));
        Assert.assertFalse(cacheStore.containsElement(key, "testValue"));
        Assert.assertFalse(cacheStore.removeElement(key, "testValue"));
    }

    @Test
    public void addAndGetTest() {
        final ListCacheStore<Integer> cacheStore = new CopyOnWriteArrayListCacheStore<>();
        final String key = "test01";
        List<Integer> numbers = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        // getElement/getElementsByRange Cache不存在测试
        Assert.assertNull(cacheStore.getElement(key, 0));
        Assert.assertNull(cacheStore.getElementsByRange(key, 0, 1));

        // addElement/getElement 正常情况测试
        Assert.assertTrue("addElement operation failed!", cacheStore.addElement(key, 0));
        Assert.assertEquals(new Integer(0), cacheStore.getElement(key, 0));
        // 超出范围的 null 测试
        Assert.assertNull(cacheStore.getElement(key, cacheStore.elementsLength(key)));

        // addElements/getElementsByRange 正常情况测试
        Assert.assertTrue("addElements operation failed!", cacheStore.addElements(key, numbers));
        Assert.assertEquals(Lists.newArrayList(0, 1, 2), cacheStore.getElementsByRange(key, 0, 3));

        // 不足长度的 getElementsByRange
        Assert.assertEquals(Lists.newArrayList(7, 8, 9), cacheStore.getElementsByRange(key, 7, 8));

        // 超出索引的 getElementsByRange
        List<Integer> result = cacheStore.getElementsByRange(key, cacheStore.elementsLength(key) + 1, 8);
        Assert.assertNotNull("getElementsByRange returned null if index is out of range", result);
        Assert.assertEquals("getElementsByRange returned a non empty list when the index was out of range",
                0, result.size());

        // 不足长度的 getElementsByRange
        Assert.assertEquals(Lists.newArrayList(), cacheStore.getElementsByRange(key, cacheStore.elementsLength(key), 0));
    }

    @Test
    public void removeElementTest() {
        // removeElement(String, E) / removeElement(String, int)
        final ListCacheStore<String> cacheStore = new CopyOnWriteArrayListCacheStore<>();
        final String key = "test01";
        Random random = new Random();
        List<String> numbers = Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9");

        // 删除不存在 Cache 返回 false
        Assert.assertFalse(cacheStore.removeElement(key, 0));

        Assert.assertTrue("addElements operation failed!", cacheStore.addElements(key, numbers));

        int removeIndex = random.nextInt(cacheStore.elementsLength(key));
        numbers.remove(removeIndex);
        Assert.assertTrue("removeElement operation failed!", cacheStore.removeElement(key, removeIndex));
        Assert.assertEquals(numbers, cacheStore.getElementsByRange(key, 0, cacheStore.elementsLength(key)));

        String removeTarget = cacheStore.getElement(key, random.nextInt(cacheStore.elementsLength(key)));
        Assert.assertNotNull(removeTarget);
        Assert.assertTrue(cacheStore.containsElement(key, removeTarget));
        numbers.remove(removeTarget);
        Assert.assertTrue("removeElement operation failed!", cacheStore.removeElement(key, removeTarget));
        Assert.assertEquals(numbers, cacheStore.getElementsByRange(key, 0, cacheStore.elementsLength(key)));

        Assert.assertTrue("clearCollection operation failed!", cacheStore.clearCollection(key));
        Assert.assertTrue(cacheStore.exists(key));
        Assert.assertEquals(0, cacheStore.elementsLength(key));
        Assert.assertTrue(cacheStore.isEmpty(key));

        // 删除不存在元素返回 false
        Assert.assertFalse(cacheStore.removeElement(key, cacheStore.elementsLength(key)));
    }

}