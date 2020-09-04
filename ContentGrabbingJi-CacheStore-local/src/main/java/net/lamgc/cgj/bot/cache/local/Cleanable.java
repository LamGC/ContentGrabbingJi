package net.lamgc.cgj.bot.cache.local;

/**
 * 可清理接口, 实现该接口代表该类具有清理动作.
 * @author LamGC
 */
public interface Cleanable {

    /**
     * 该方法需要CacheStore完成对过期Entry的清除.
     * @return 返回已清理数量.
     * @throws Exception 即使该方法抛出异常, 也不会影响后续情况.
     */
    long clean() throws Exception;

}
