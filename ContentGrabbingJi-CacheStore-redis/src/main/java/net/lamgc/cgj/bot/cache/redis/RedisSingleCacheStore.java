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

import java.util.Objects;

/**
 * Redis 单项缓存存储容器.
 * @param <V> 值类型.
 * @see net.lamgc.cgj.bot.cache.CacheStore
 * @see net.lamgc.cgj.bot.cache.SingleCacheStore
 * @see net.lamgc.cgj.bot.cache.redis.RedisCacheStore
 * @author LamGC
 */
public class RedisSingleCacheStore<V> extends RedisCacheStore<V> implements SingleCacheStore<V> {

    private final String keyPrefix;
    private final StringConverter<V> converter;
    private final RedisConnectionPool connectionPool;

    public RedisSingleCacheStore(RedisConnectionPool connectionPool, String keyPrefix, StringConverter<V> converter) {
        super(connectionPool);
        this.connectionPool = connectionPool;
        keyPrefix = Objects.requireNonNull(keyPrefix).trim();
        if (keyPrefix.isEmpty()) {
            throw new IllegalArgumentException("Key prefix cannot be empty.");
        }
        if (keyPrefix.endsWith(RedisUtils.KEY_SEPARATOR)) {
            this.keyPrefix = keyPrefix;
        } else {
            this.keyPrefix = keyPrefix + RedisUtils.KEY_SEPARATOR;
        }

        this.converter = Objects.requireNonNull(converter);
    }

    @Override
    public boolean set(CacheKey key, V value) {
        return connectionPool.executeRedis(jedis ->
                RedisUtils.isOk(jedis.set(getKeyString(key), converter.to(Objects.requireNonNull(value)))));
    }

    @Override
    public boolean setIfNotExist(CacheKey key, V value) {
        return connectionPool.executeRedis(jedis ->
                jedis.setnx(getKeyString(key), converter.to(Objects.requireNonNull(value)))
                        == RedisUtils.RETURN_CODE_OK);
    }

    @Override
    public V get(CacheKey key) {
        String value = connectionPool.executeRedis(jedis -> jedis.get(getKeyString(key)));
        if (value == null) {
            return null;
        }
        return converter.from(value);
    }

    @Override
    protected String getKeyPrefix() {
        return this.keyPrefix;
    }
}
