package net.lamgc.cgj.bot.cache;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于Hashtable的本地缓存库
 * @param <T> 缓存类型
 */
public class LocalHashCacheStore<T> implements CacheStore<T>, Cleanable {

    private final Hashtable<String, CacheObject<T>> cache;

    /**
     * 构造一个基于Hashtable的本地缓存库
     * @see Hashtable
     */
    public LocalHashCacheStore() {
        this(0);
    }

    /**
     * 构造一个基于Hashtable的本地缓存库
     * @param initialCapacity 初始容量
     * @see Hashtable
     */
    public LocalHashCacheStore(int initialCapacity) {
        this(initialCapacity, 0F);
    }

    /**
     * 构造一个基于Hashtable的本地缓存库
     * @param initialCapacity 初始容量
     * @param loadFactor 重载因子
     * @see Hashtable
     */
    public LocalHashCacheStore(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, false);
    }

    /**
     * 构造一个基于Hashtable的本地缓存库
     * @param initialCapacity 初始容量
     * @param loadFactor 重载因子
     * @param autoClean 是否自动清理
     * @see Hashtable
     */
    public LocalHashCacheStore(int initialCapacity, float loadFactor, boolean autoClean) {
        if(initialCapacity != 0) {
            if(loadFactor <= 0F) {
                cache = new Hashtable<>(initialCapacity);
            } else {
                cache = new Hashtable<>(initialCapacity, loadFactor);
            }
        } else {
            cache = new Hashtable<>();
        }

        if(autoClean) {
            AutoCleanTimer.add(this);
        }
    }

    @Override
    public void update(String key, T value, long expire) {
        update(key, value, expire <= 0 ? null : new Date(System.currentTimeMillis() + expire));
    }

    @Override
    public void update(String key, T value, Date expire) {
        if(cache.containsKey(key)) {
            cache.get(key).update(value, expire);
        } else {
            CacheObject<T> cacheObject = new CacheObject<>(value, expire);
            cache.put(key, cacheObject);
        }
    }

    @Override
    public T getCache(String key) {
        if(!cache.containsKey(key)) {
            return null;
        }
        CacheObject<T> cacheObject = cache.get(key);
        if(cacheObject.isExpire(new Date())) {
            cache.remove(key);
            return null;
        }
        return cacheObject.get();
    }

    @Override
    public T getCache(String key, long index, long length) {
        return getCache(key);
    }

    @Override
    public boolean exists(String key) {
        return exists(key, null);
    }

    @Override
    public boolean exists(String key, Date date) {
        if(!cache.containsKey(key)) {
            return false;
        }
        CacheObject<T> cacheObject = cache.get(key);
        if(cacheObject.isExpire(Objects.isNull(date) ? new Date() : date)) {
            cache.remove(key);
            return false;
        }
        return true;
    }

    @Override
    public long length(String key) {
        return -1;
    }

    @Override
    public boolean clear() {
        cache.clear();
        return true;
    }

    @Override
    public Set<String> keys() {
        return cache.keySet();
    }

    @Override
    public boolean remove(String key) {
        return cache.remove(key) != null;
    }

    @Override
    public boolean supportedPersistence() {
        return false;
    }

    @Override
    public boolean supportedList() {
        return false;
    }

    @Override
    public void clean() {
        Date currentDate = new Date();
        Set<String> expireKeySet = new HashSet<>();
        cache.forEach((key, value) -> {
            if(value.isExpire(currentDate)) {
                expireKeySet.add(key);
            }
        });

        expireKeySet.forEach(cache::remove);
    }

    public static class CacheObject<T> implements Comparable<CacheObject<T>> {

        private final AtomicReference<T> value;
        private final AtomicReference<Date> expire;

        public CacheObject(T value, Date expire) {
            this.value = new AtomicReference<>(value);
            this.expire = new AtomicReference<>(expire);
        }

        public synchronized void update(T value, Date newExpire) {
            if(new Date().after(newExpire)) {
                throw new IllegalArgumentException("Due earlier than current time");
            }
            this.expire.set(newExpire);
            this.value.set(value);
        }

        public synchronized T get() {
            return value.get();
        }

        public Date getExpireDate() {
            return expire.get();
        }

        public boolean isExpire(Date time) {
            Date expireDate = getExpireDate();
            return expireDate != null && expireDate.before(time);
        }

        @Override
        public int compareTo(@NotNull CacheObject<T> o) {
            return this.getExpireDate().after(o.getExpireDate()) ? -1 : 1;
        }
    }

}
