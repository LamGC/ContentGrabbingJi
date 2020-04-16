package net.lamgc.cgj.bot.cache;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class LocalHashCacheStore<T> implements CacheStore<T> {

    private final Hashtable<String, CacheObject<T>> cache;

    public LocalHashCacheStore() {
        this(0);
    }

    public LocalHashCacheStore(int initialCapacity) {
        this(initialCapacity, 0F);
    }

    public LocalHashCacheStore(int initialCapacity, float loadFactor) {
        if(initialCapacity != 0) {
            if(loadFactor <= 0F) {
                cache = new Hashtable<>(initialCapacity);
            } else {
                cache = new Hashtable<>(initialCapacity, loadFactor);
            }
        } else {
            cache = new Hashtable<>();
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
    public boolean supportedPersistence() {
        return false;
    }

    @Override
    public boolean supportedList() {
        return false;
    }


    public static class CacheObject<T> implements Comparable<CacheObject<T>> {

        private AtomicReference<T> value;
        private AtomicReference<Date> expire;

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
