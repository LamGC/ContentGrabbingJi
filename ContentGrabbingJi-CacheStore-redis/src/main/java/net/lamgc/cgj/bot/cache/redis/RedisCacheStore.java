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
import net.lamgc.cgj.bot.cache.CacheStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author LamGC
 */
public abstract class RedisCacheStore<V> implements CacheStore<V> {

    private final RedisConnectionPool connectionPool;

    protected RedisCacheStore(RedisConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * 获取 Key 前缀.
     * <p>key = getKeyPrefix() + key
     * @param cacheKey CacheKey 对象.
     * @return 返回 Key 前缀.
     */
    protected String getKeyString(CacheKey cacheKey) {
        return RedisUtils.toRedisCacheKey(getKeyPrefix(), cacheKey);
    }

    /**
     * 获取 Key 的完整前缀.
     * @return 返回完整前缀.
     */
    protected abstract String getKeyPrefix();

    @Override
    public boolean setTimeToLive(CacheKey key, long ttl) {
        String keyString = getKeyString(key);
        return connectionPool.executeRedis(jedis -> {
            Long result;
            if (ttl >= 0) {
                result = jedis.pexpire(keyString, ttl);
            } else {
                result = jedis.persist(keyString);
            }
            return result.intValue() == RedisUtils.RETURN_CODE_OK;
        });
    }

    @Override
    public long getTimeToLive(CacheKey key) {
        return connectionPool.executeRedis(jedis -> {
            Long ttl = jedis.pttl(getKeyString(key));
            return ttl < 0 ? -1 : ttl;
        });
    }

    @Override
    public long size() {
        return (long) connectionPool.executeRedis(jedis -> jedis.keys(getKeyString(RedisUtils.CACHE_KEY_ALL)).size());
    }

    @Override
    public boolean clear() {
        List<String> keys = new ArrayList<>(1);
        keys.add(getKeyString(RedisUtils.CACHE_KEY_ALL));
        connectionPool.executeScript(LuaScript.STORE_REMOVE_KEYS_BY_PREFIX, keys, null);
        return true;
    }

    @Override
    public boolean exists(CacheKey key) {
        return connectionPool.executeRedis(jedis -> jedis.exists(getKeyString(key)));
    }

    @Override
    public boolean remove(CacheKey key) {
        return connectionPool.executeRedis(jedis -> jedis.del(getKeyString(key)) == RedisUtils.RETURN_CODE_OK);
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = connectionPool.executeRedis(jedis ->
                jedis.keys(getKeyString(RedisUtils.CACHE_KEY_ALL)));
        final int prefixLength = getKeyPrefix().length();
        Set<String> newKeys = new HashSet<>();
        for (String key : keys) {
            newKeys.add(key.substring(prefixLength));
        }
        return newKeys;
    }

}
