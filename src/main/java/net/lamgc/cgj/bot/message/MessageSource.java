package net.lamgc.cgj.bot.message;

/**
 * 消息来源
 */
public enum MessageSource {
    /**
     * 私聊消息
     */
    Private,
    /**
     * 群组消息
     */
    Group,
    /**
     * 讨论组消息
     */
    Discuss,
    /**
     * 未知来源
     */
    Unknown
}