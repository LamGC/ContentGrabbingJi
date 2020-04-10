package net.lamgc.cgj.bot.cache;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class CacheObject<T> {

    private AtomicReference<T> value;
    private AtomicReference<Date> expire;

    public CacheObject() {
        this(null, null);
    }

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
}
