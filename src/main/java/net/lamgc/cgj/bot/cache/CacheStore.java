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
     * 针对列表缓存的获取
     * @param key 键名
     * @param index 起始索引
     * @param length 选取长度
     * @return 返回指定类型, 如不支持, 该方法的行为应与{@linkplain #getCache(String) getCache(String)}一致
     */
    T getCache(String key, long index, long length);

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
     * 查询列表缓存的长度
     * @param key 键名
     * @return 返回指定类型, 如不支持或不存在, 将返回-1
     */
    long length(String key);

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

    /**
     * 是否支持列表缓存.
     * 当本方法返回true时, {@link #length(String)}和{@link #getCache(String, long, long)}必须有具体实现而不能做兼容性处理.
     * @return 如果支持返回true
     */
    boolean supportedList();

}
