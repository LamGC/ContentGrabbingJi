package net.lamgc.cgj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public final class Locker<K> {

    private final static Logger log = LoggerFactory.getLogger(Locker.class);

    private final LockerMap<K> fromMap;

    private final K key;

    private final boolean autoDestroy;

    private final AtomicInteger lockCount = new AtomicInteger(0);

    /**
     * 构造一个锁对象
     * @param map 所属LockerMap
     * @param key 所属Key
     */
    Locker(LockerMap<K> map, K key, boolean autoDestroy) {
        this.fromMap = map;
        this.key = key;
        this.autoDestroy = autoDestroy;
    }

    /**
     * 上锁
     */
    public void lock() {
        lockCount.incrementAndGet();
    }

    /**
     * 解锁
     */
    public void unlock() {
        int newValue = lockCount.decrementAndGet();
        if(newValue <= 0 && autoDestroy) {
            destroy();
        }
    }

    /**
     * 获取锁对象所属Key
     */
    public K getKey() {
        return key;
    }

    /**
     * 销毁锁对象
     */
    public void destroy() {
        fromMap.destroyLocker(this);
    }

    @Override
    public String toString() {
        return "Locker@" + this.hashCode() + "{" +
                "fromMap=" + fromMap +
                ", key=" + key +
                '}';
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        log.trace("{} 已销毁.", this.toString());
    }
}
