package net.lamgc.cgj.bot.cache;

import java.util.Date;

public interface CacheStore<T> {

    /**
     * 更新或添加缓存项
     * @param key 缓存键名
     * @param value 缓存值
     * @param expire 有效期, 单位为ms(毫秒), 如不过期传入0或赋值
     */
    void update(String key, T value, long expire);

    /**
     * 更新或添加缓存项
     * @param key 缓存键名
     * @param value 缓存值
     * @param expire 过期时间, 如不过期传入null
     */
    void update(String key, T value, Date expire);

    /**
     * 获取缓存数据
     * @param key 键名
     * @return 如果存在, 返回对象, 不存在或获取失败则返回null
     */
    T getCache(String key);

    /**
     * 如果该键存在且未过期则返回true.
     * @param key 要查询的键
     * @return 如果存在且未过期则返回true
     */
    boolean exists(String key);

    /**
     * 如果该键存在且未过期则返回true
     * @param key 要查询的键
     * @param date 检查的时间
     * @return 如果存在且未过期则返回true
     */
    boolean exists(String key, Date date);

    /**
     * 清空缓存
     * @return 如果清空成功, 返回true
     */
    boolean clear();

    /**
     * 是否支持持久化
     * @return 如果支持返回true
     */
    boolean supportedPersistence();

}
