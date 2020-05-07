package net.lamgc.cgj.bot.cache;

/**
 * 可清理接口, 实现该接口代表该类拥有清理动作.
 */
public interface Cleanable {

    void clean() throws Exception;

}
