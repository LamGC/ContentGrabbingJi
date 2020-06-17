package net.lamgc.cgj.util;

import java.util.HashMap;

public class LockerMap<K> {

    private final HashMap<K, Locker<K>> lockerHashMap = new HashMap<>();

    /**
     * 创建锁
     * @param key Key
     * @return 如果Key所属锁存在, 则返回对应锁, 否则返回新锁
     */
    public Locker<K> createLocker(K key, boolean autoDestroy) {
        if(lockerHashMap.containsKey(key)) {
            return lockerHashMap.get(key);
        }
        Locker<K> newLocker = new Locker<>(this, key, autoDestroy);
        lockerHashMap.put(key, newLocker);
        return newLocker;
    }

    /**
     * 销毁锁
     * @param locker 锁对象
     */
    public void destroyLocker(Locker<K> locker) {
        lockerHashMap.remove(locker.getKey());
    }

}
