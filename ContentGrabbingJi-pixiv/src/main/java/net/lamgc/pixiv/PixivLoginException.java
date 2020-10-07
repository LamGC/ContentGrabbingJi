package net.lamgc.pixiv;

/**
 * Pixiv 登录异常.
 * @author LamGC
 */
public class PixivLoginException extends Exception {

    public PixivLoginException(String message) {
        super(message);
    }

    public PixivLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
