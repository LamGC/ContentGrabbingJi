package net.lamgc.cgj.bot.cache.local;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时清理机制.
 * <p>定时通知已实现 {@link Cleanable} 接口的对象进行清理.
 * @see Cleanable
 * @author LamGC
 */
public class AutoCleanTimer implements Runnable {

    private final static Set<WeakReference<Cleanable>> CLEANABLE_STORE_SET = new CopyOnWriteArraySet<>();

    private final static ScheduledExecutorService SCHEDULED_EXECUTOR =
            new ScheduledThreadPoolExecutor(1,
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("Thread-AutoClean-%d")
                            .build()
            );

    private final static Logger log = LoggerFactory.getLogger(AutoCleanTimer.class);

    static {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(new AutoCleanTimer(), 100L, 100L, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(SCHEDULED_EXECUTOR::shutdownNow, "ShutdownThread-AutoClean"));
    }

    /**
     * 增加需要定时执行清理的缓存库
     * @param store 已实现Cleanable的对象
     */
    public static void add(Cleanable store) {
        CLEANABLE_STORE_SET.add(new WeakReference<>(store));
    }

    private AutoCleanTimer() {}

    @Override
    public void run() {
        if (CLEANABLE_STORE_SET.size() == 0) {
            return;
        }

        Iterator<WeakReference<Cleanable>> iterator = CLEANABLE_STORE_SET.iterator();
        Set<WeakReference<Cleanable>> toBeCleanReference = new HashSet<>();
        while(iterator.hasNext()) {
            WeakReference<Cleanable> reference = iterator.next();
            Cleanable store = reference.get();
            if (store == null) {
                // 由于 COW ArraySet 的 Iterator 不支持 remove 操作,
                // 所以先收集起来, 等完成所有清理工作后统一删除引用.
                toBeCleanReference.add(reference);
                return;
            }
            try {
                store.clean();
            } catch (Exception e) {
                log.error("{} 执行清理动作时发生异常:\n{}", store.toString(), Throwables.getStackTraceAsString(e));
            }
        }
        CLEANABLE_STORE_SET.removeAll(toBeCleanReference);
    }
}
