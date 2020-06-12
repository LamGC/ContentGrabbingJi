package net.lamgc.cgj.bot.message;

/**
 * 消息来源
 */
public enum MessageSource {
    /**
     * 私聊消息
     */
    PRIVATE,
    /**
     * 群组消息
     */
    GROUP,
    /**
     * 讨论组消息
     */
    DISCUSS,
    /**
     * 未知来源
     */
    UNKNOWN
}