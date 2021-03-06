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

package net.lamgc.cgj.bot.cache.local;

import net.lamgc.cgj.bot.cache.CacheKey;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @see HashSetCacheStore
 */
public class HashSetCacheStoreTest {

    @Test
    public void getCacheItemCollection() {
        HashSetCacheStore<String> store = new HashSetCacheStore<>();
        final CacheKey key = new CacheKey("test");
        Assert.assertNull(store.getCacheItemCollection(key, false));

        Set<String> collection = store.getCacheItemCollection(key, true);
        Assert.assertNotNull(collection);

        Assert.assertEquals(collection, store.getCacheItemCollection(key, false));
    }
}