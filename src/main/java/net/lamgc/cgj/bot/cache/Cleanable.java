package net.lamgc.cgj.bot.cache;

/**
 * 可清理接口, 实现该接口代表该类具有清理动作.
 */
public interface Cleanable {

    /**
     * 该方法需要CacheStore完成对过期Entry的清除.
     */
    void clean() throws Exception;

}
