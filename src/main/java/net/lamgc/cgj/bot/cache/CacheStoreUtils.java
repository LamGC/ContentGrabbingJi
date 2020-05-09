package net.lamgc.cgj.bot.cache;

public final class CacheStoreUtils {

    private CacheStoreUtils() {}

    /**
     * 将 CacheStore 转换成 基于 {@link LocalHashCacheStore} 的 {@link HotDataCacheStore}
     * <p>通过该方法转换, 会自动启用 自动清理</p>
     * @param cacheStore 上游缓存库
     * @param expireTime 热点缓存最小有效期
     * @param floatRange 缓存浮动最大范围
     * @param <T> 缓存库数据类型
     * @return 返回 {@link HotDataCacheStore}
     */
    public static <T> CacheStore<T> hashLocalHotDataStore(CacheStore<T> cacheStore, long expireTime, int floatRange) {
        return hashLocalHotDataStore(cacheStore, expireTime, floatRange, true);
    }

    /**
     * 将 CacheStore 转换成 基于 {@link LocalHashCacheStore} 的 {@link HotDataCacheStore}
     * @param cacheStore 上游缓存库
     * @param expireTime 热点缓存最小有效期
     * @param floatRange 缓存浮动最大范围
     * @param autoClean 是否启用自动清理
     * @param <T> 缓存库数据类型
     * @return 返回 {@link HotDataCacheStore}
     */
    public static <T> CacheStore<T> hashLocalHotDataStore(CacheStore<T> cacheStore,
                                                          long expireTime, int floatRange, boolean autoClean) {
        return new HotDataCacheStore<>(cacheStore, new LocalHashCacheStore<>(), expireTime, floatRange, autoClean);
    }


}
