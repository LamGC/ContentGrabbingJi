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
import net.lamgc.cgj.bot.cache.MapCacheStore;
import net.lamgc.cgj.bot.cache.convert.StringConverter;

import java.util.*;

/**
 * Redis Map缓存存储容器.
 * @param <V> 值类型.
 * @author LamGC
 */
public class RedisMapCacheStore<V> extends RedisCacheStore<Map<String, V>> implements MapCacheStore<V> {

    private final String keyPrefix;
    private final StringConverter<V> converter;
    private final RedisConnectionPool connectionPool;

    public RedisMapCacheStore(RedisConnectionPool connectionPool, String keyPrefix, StringConverter<V> converter) {
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
    protected String getKeyPrefix() {
        return this.keyPrefix;
    }

    @Override
    public int mapSize(CacheKey key) {
        return connectionPool.executeRedis(jedis -> {
            String keyString = getKeyString(key);
            if (jedis.exists(keyString)) {
                return jedis.hlen(keyString).intValue();
            }
            return 0;
        });
    }

    @Override
    public Set<String> mapFieldSet(CacheKey key) {
        return connectionPool.executeRedis(jedis -> {
            String keyString = getKeyString(key);
            if (jedis.exists(keyString)) {
                return jedis.hkeys(keyString);
            }
            return null;
        });
    }

    @Override
    public Set<V> mapValueSet(CacheKey key) {
        List<String> rawValueSet = connectionPool.executeRedis(jedis -> {
            String keyString = getKeyString(key);
            if (jedis.exists(keyString)) {
                return jedis.hvals(keyString);
            }
            return null;
        });

        if (rawValueSet == null) {
            return null;
        }

        Set<V> result = new HashSet<>();
        for (String rawValue : rawValueSet) {
            result.add(converter.from(rawValue));
        }
        return result;
    }

    @Override
    public boolean put(CacheKey key, String field, V value) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(value);
        connectionPool.executeRedis(jedis -> {
            String keyString = getKeyString(key);
            return jedis.hset(keyString, field, converter.to(value));
        });
        return true;
    }

    @Override
    public boolean putAll(CacheKey key, Map<? extends String, ? extends V> map) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(map);
        if (map.size() == 0) {
            return exists(key);
        }

        final Map<String, String> targetMap = new HashMap<>(map.size());
        map.forEach((k, v) -> targetMap.put(k, converter.to(v)));
        return connectionPool.executeRedis(jedis -> {
            String keyString = getKeyString(key);
            return RedisUtils.isOk(jedis.hmset(keyString, targetMap));
        });
    }

    @Override
    public boolean putIfNotExist(CacheKey key, String field, V value) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(value);
        return connectionPool.executeRedis(jedis -> {
            String keyString = getKeyString(key);
            return jedis.hsetnx(keyString, field, converter.to(value)) == RedisUtils.RETURN_CODE_OK;
        });
    }

    @Override
    public V get(CacheKey key, String field) {
        Objects.requireNonNull(field);
        String value = connectionPool.executeRedis(jedis -> jedis.hget(getKeyString(key), field));
        if (value == null) {
            return null;
        }
        return converter.from(value);
    }

    @Override
    public boolean removeField(CacheKey key, String field) {
        Objects.requireNonNull(field);
        return connectionPool.executeRedis(jedis ->
                jedis.hdel(getKeyString(key), field) == RedisUtils.RETURN_CODE_OK);
    }

    @Override
    public boolean containsField(CacheKey key, String field) {
        Objects.requireNonNull(field);
        return connectionPool.executeRedis(jedis -> jedis.hexists(getKeyString(key), field));
    }

    @Override
    public boolean mapIsEmpty(CacheKey key) {
        return mapSize(key) == 0;
    }

    @Override
    public boolean clearMap(CacheKey key) {
        return remove(key);
    }

}
