package net.lamgc.cgj.bot.framework;

public interface Framework {

    /**
     * 框架初始化方法
     * @param resources 框架所分配到的资源.
     * @throws Exception 当框架抛出异常时, 将不会继续运行框架.
     * @see FrameworkManager.FrameworkResources
     */
    void init(FrameworkManager.FrameworkResources resources) throws Exception;

    /**
     * 框架运行方法
     * @throws Exception
     */
    void run() throws Exception;

    void close() throws Exception;

    String getName();
}
