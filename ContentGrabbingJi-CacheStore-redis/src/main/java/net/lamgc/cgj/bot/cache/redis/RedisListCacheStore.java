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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Redis 列表缓存存储容器.
 * @param <E>
 * @author LamGC
 */
public class RedisListCacheStore<E> extends RedisCacheStore<List<E>> implements ListCacheStore<E> {

    private final String keyPrefix;
    private final StringConverter<E> converter;
    private final RedisConnectionPool connectionPool;

    public RedisListCacheStore(RedisConnectionPool connectionPool, String keyPrefix, StringConverter<E> converter) {
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
    public E getElement(CacheKey key, int index) {
        return connectionPool.executeRedis(jedis -> converter.from(jedis.lindex(getKeyString(key), index)));
    }

    @Override
    public List<E> getElementsByRange(CacheKey key, int index, int length) {
        List<String> strings = connectionPool.executeRedis(jedis ->
                // stop = start + length - 1
                jedis.lrange(getKeyString(key), index, index + length - 1));
        List<E> result = new ArrayList<>(strings.size());
        strings.forEach(element -> result.add(converter.from(element)));
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>注意: 在 Redis 实现中, 该功能通过一段 Lua 脚本实现,
     * 由于 Redis 并没有原生支持该功能, 所以只能用脚本遍历查找.
     * 如果 List 元素过多, 可能会导致执行缓慢且影响后续操作, 谨慎使用.
     * @param key 待操作的缓存项键名.
     * @param index 欲删除元素的索引, 从 0 开始.
     * @return 如果元素存在且删除成功, 返回 true.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    @Override
    public boolean removeElement(CacheKey key, int index) {
        List<String> keys = new ArrayList<>(1);
        List<String> args = new ArrayList<>(1);
        keys.add(getKeyString(key));
        args.add(String.valueOf(index));
        Number result = (Number) connectionPool.executeScript(LuaScript.LIST_REMOVE_ELEMENT_BY_INDEX, keys, args);
        return result.intValue() == 1;
    }

    @Override
    public boolean removeElement(CacheKey key, E element) {
        return connectionPool.executeRedis(jedis ->
                jedis.lrem(getKeyString(key), 1, converter.to(element)) != RedisUtils.RETURN_CODE_FAILED);
    }

    @Override
    public boolean addElement(CacheKey key, E element) {
        Objects.requireNonNull(element);
        return connectionPool.executeRedis(jedis ->
                jedis.lpush(getKeyString(key), converter.to(element)) != RedisUtils.RETURN_CODE_FAILED);
    }

    @Override
    public boolean addElements(CacheKey key, Collection<E> elements) {
        Objects.requireNonNull(elements);
        if (elements.size() == 0) {
            return exists(key);
        }

        List<E> values = new ArrayList<>(elements);
        String[] valueStrings = new String[values.size()];
        for (int i = 0; i < valueStrings.length; i++) {
            valueStrings[i] = converter.to(values.get(i));
        }

        return connectionPool.executeRedis(jedis ->
                jedis.lpush(getKeyString(key), valueStrings) != RedisUtils.RETURN_CODE_FAILED);
    }

    /**
     * {@inheritDoc}
     *
     * <p>注意: 在 Redis 实现中, 该功能通过一段 Lua 脚本实现,
     * 由于 Redis 并没有原生支持该功能, 所以只能用脚本遍历查找.
     * 如果 List 元素过多, 可能会导致执行缓慢且影响后续操作, 谨慎使用.
     * @param key 待检查的缓存项键名.
     * @param element 待查找的缓存值.
     * @return 如果存在, 返回 true, 如果元素不存在, 或缓存项不存在, 返回 false.
     * @throws NullPointerException 当 key 或 element 为 null 时抛出; 本方法不允许存储 null 值, 因为 null 代表"没有/不存在".
     */
    @Override
    public boolean containsElement(CacheKey key, E element) {
        List<String> keys = new ArrayList<>(1);
        List<String> args = new ArrayList<>(1);
        keys.add(getKeyString(key));
        args.add(converter.to(element));
        Number result = (Number) connectionPool.executeScript(LuaScript.LIST_CHECK_ELEMENT_CONTAINS, keys, args);
        return result.intValue() != -1;
    }

    @Override
    public boolean isEmpty(CacheKey key) {
        return elementsLength(key) == -1;
    }

    @Override
    public int elementsLength(CacheKey key) {
        long result = connectionPool.executeRedis(jedis -> jedis.llen(getKeyString(key)));
        if (result == 0) {
            return -1;
        } else {
            return (int) result;
        }
    }

    @Override
    public boolean clearCollection(CacheKey key) {
        return remove(key);
    }

    @Override
    protected String getKeyPrefix() {
        return this.keyPrefix;
    }
}
