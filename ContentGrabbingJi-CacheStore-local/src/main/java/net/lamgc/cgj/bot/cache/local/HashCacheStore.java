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

import net.lamgc.cgj.bot.cache.CacheKey;
import net.lamgc.cgj.bot.cache.CacheStore;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于 {@link Hashtable} 的缓存存储容器.
 * @param <V> 值类型.
 * @author LamGC
 * @see net.lamgc.cgj.bot.cache.CacheStore
 * @see Hashtable
 */
public abstract class HashCacheStore<V> implements CacheStore<V>, Cleanable {

    private final Map<String, CacheItem<V>> cacheMap = new Hashtable<>();

    /**
     * 获取内部 Map 对象.
     * 仅供其他子类使用.
     * @return 返回存储缓存项的 Map.
     */
    protected Map<String, CacheItem<V>> getCacheMap() {
        return cacheMap;
    }

    @Override
    public boolean setTimeToLive(CacheKey key, long ttl) {
        if (!exists(key)) {
            return false;
        }
        CacheItem<V> item = cacheMap.get(key.toString());
        item.setExpireDate(ttl < 0 ? null : new Date(System.currentTimeMillis() + ttl));
        return true;
    }

    @Override
    public long getTimeToLive(CacheKey key) {
        if (!exists(key)) {
            return -1;
        }
        CacheItem<V> item = cacheMap.get(key.toString());
        Date expireDate = item.getExpireDate();
        if (expireDate != null) {
            return expireDate.getTime() - System.currentTimeMillis();
        }
        return -1;
    }

    @Override
    public long size() {
        return cacheMap.size();
    }

    @Override
    public boolean clear() {
        cacheMap.clear();
        return true;
    }

    @Override
    public boolean exists(CacheKey key) {
        if (!cacheMap.containsKey(key.toString())) {
            return false;
        }
        CacheItem<V> item = cacheMap.get(key.toString());
        // 在检查其过期情况后根据情况进行清理, 减轻主动清理机制的负担.
        if (item.isExpire(new Date())) {
            remove(key);
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(CacheKey key) {
        // 根据 Collection 说明, 删除时 key 存在映射就会返回, 只要返回 null 就代表没有.
        return cacheMap.remove(key.toString()) != null;
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(cacheMap.keySet());
    }

    @Override
    public long clean() {
        Map<String, CacheItem<V>> cacheMap = getCacheMap();
        Date currentDate = new Date();
        AtomicLong cleanCount = new AtomicLong(0);
        cacheMap.keySet().removeIf(key -> {
            CacheItem<V> item = cacheMap.get(key);
            if (item.isExpire(currentDate)) {
                cleanCount.incrementAndGet();
                return true;
            }
            return false;
        });
        return cleanCount.get();
    }

    /**
     * 缓存项.
     * @author LamGC
     */
    protected final static class CacheItem<V> {
        private final V value;
        private Date expireDate;

        CacheItem(V value) {
            this(value, null);
        }

        CacheItem(V value, Date expireDate) {
            this.value = value;
            this.expireDate = expireDate;
        }

        public V getValue() {
            return value;
        }

        public void setExpireDate(Date expireDate) {
            this.expireDate = expireDate;
        }

        public Date getExpireDate() {
            return expireDate;
        }

        /**
         * 检查缓存项是否过期.
         * @param date 当前时间.
         * @return 如果已设置过期时间且早于提供的Date, 则该缓存项过期, 返回 true.
         * @throws NullPointerException 当 date 传入 null 时抛出.
         */
        public boolean isExpire(Date date) {
            Date expireDate = getExpireDate();
            return expireDate != null && expireDate.before(Objects.requireNonNull(date));
        }

    }

}
