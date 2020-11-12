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

package net.lamgc.cgj.bot.cache;

import org.junit.Assert;
import org.junit.Test;

public class CacheKeyTest {

    @Test
    public void hashCodeAndEqualsTest() {
        CacheKey key = new CacheKey("test");
        Assert.assertEquals(key, key);
        Assert.assertNotEquals(key, null);
        Assert.assertNotEquals(key, new Object());
        Assert.assertEquals(new CacheKey("test", "key01"), new CacheKey("test", "key01"));
        Assert.assertEquals(new CacheKey("test", "key01").hashCode(), new CacheKey("test", "key01").hashCode());
        Assert.assertNotEquals(new CacheKey("test", "key01"), new CacheKey("test", "key00"));
        Assert.assertNotEquals(new CacheKey("test", "key01").hashCode(), new CacheKey("test", "key00").hashCode());
        Assert.assertEquals(new CacheKey("test", "key01").toString(), "test.key01");
    }

    @Test
    public void buildTest() {
        Assert.assertThrows(NullPointerException.class, () -> new CacheKey(null));
        Assert.assertThrows(NullPointerException.class, () -> new CacheKey(null, new String[0]));
        Assert.assertThrows(IllegalArgumentException.class, () -> new CacheKey(new String[0]));
        final String[] keys = new String[] {"test", "key01"};
        Assert.assertArrayEquals(new CacheKey(keys).getKeyArray(), keys);
    }

    @Test
    public void joinTest() {
        Assert.assertEquals("test.key01", new CacheKey("test", "key01").join("."));
        Assert.assertEquals("test:key01", new CacheKey("test", "key01").join(":"));
    }

    @Test
    public void nullValueCheckTest() {
        CacheKey test = new CacheKey("test", (String[]) null);
        Assert.assertEquals("test", test.toString());
        Assert.assertThrows(NullPointerException.class, () -> new CacheKey("test", (String) null));
    }

}
