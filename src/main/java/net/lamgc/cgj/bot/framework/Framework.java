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
     * @throws Exception 当框架抛出异常时, 将会终止框架的所有活动.
     */
    void run() throws Exception;

    /**
     * 关闭框架
     * @throws Exception 即使该方法抛出异常, {@link FrameworkManager}依然会尝试向框架所属的线程发起中断, 以试图清除框架资源.
     */
    void close() throws Exception;

    /**
     * 获取框架标识名
     * @return 返回标识名
     */
    default String getName() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
}
