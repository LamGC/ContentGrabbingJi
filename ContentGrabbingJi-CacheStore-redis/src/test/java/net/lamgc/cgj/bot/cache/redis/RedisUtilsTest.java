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
import org.junit.Assert;
import org.junit.Test;

/**
 * @see RedisUtils
 */
public class RedisUtilsTest {

    @Test
    public void isOkTest() {
        Assert.assertTrue(RedisUtils.isOk("OK"));
        Assert.assertTrue(RedisUtils.isOk("ok"));
        Assert.assertFalse(RedisUtils.isOk("Failed"));
    }

    @Test
    public void toRedisCacheKey() {
        final CacheKey key = new CacheKey("test");
        final String prefix = "prefix";

        Assert.assertEquals("prefix:test", RedisUtils.toRedisCacheKey(prefix, key));
        Assert.assertEquals("prefix:test", RedisUtils.toRedisCacheKey(prefix + RedisUtils.KEY_SEPARATOR, key));
    }

}
