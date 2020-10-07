package net.lamgc.pixiv;

/**
 * @author LamGC
 */
public interface PixivLogin {

    /**
     * 执行登录操作.
     * @return 返回登录后的会话对象
     * @see PixivSession
     * @throws PixivLoginException 当无法完成登录时抛出.
     */
    PixivSession login() throws PixivLoginException;

}
