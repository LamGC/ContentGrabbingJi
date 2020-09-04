package net.lamgc.cgj.bot.cache.local;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定时清理机制.
 * <p>定时通知已实现 {@link Cleanable} 接口的对象进行清理.
 * @see Cleanable
 * @author LamGC
 */
public class AutoCleanTimer implements Runnable {

    private final static Set<Cleanable> CLEANABLE_STORE_SET = new CopyOnWriteArraySet<>();

    private final static ScheduledExecutorService SCHEDULED_EXECUTOR =
            new ScheduledThreadPoolExecutor(1,
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("Thread-AutoClean-%d")
                            .build()
            );

    private final static Logger log = LoggerFactory.getLogger(AutoCleanTimer.class);

    private final static AtomicBoolean DEBUG_ENABLE = new AtomicBoolean(false);

    static {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(new AutoCleanTimer(), 100L, 100L, TimeUnit.MILLISECONDS);
        Thread shutdownHook = new Thread(SCHEDULED_EXECUTOR::shutdown);
        shutdownHook.setName("ShutdownThread-AutoClean");
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    /**
     * 是否启用调试模式.
     * <p>启用后将会打印相关日志.
     * @param enable 是否启用调试模式.
     */
    public static void setDebugEnable(boolean enable) {
        DEBUG_ENABLE.set(enable);
    }

    /**
     * 增加需要定时执行清理的缓存库
     * @param store 已实现Cleanable的对象
     */
    public static void add(Cleanable store) {
        CLEANABLE_STORE_SET.add(store);
    }

    /**
     * 移除已添加的缓存库
     * @param store 需要从AutoCleanTimer移除的对象
     */
    public static void remove(Cleanable store) {
        CLEANABLE_STORE_SET.remove(store);
    }

    private AutoCleanTimer() {}

    @Override
    public void run() {
        if (CLEANABLE_STORE_SET.size() == 0) {
            return;
        }

        CLEANABLE_STORE_SET.forEach(cleanable -> {
            try {
                cleanable.clean();
            } catch (Exception e) {
                log.error("{} 执行清理动作时发生异常:\n{}", cleanable.toString(), Throwables.getStackTraceAsString(e));
            }
        });
    }
}
