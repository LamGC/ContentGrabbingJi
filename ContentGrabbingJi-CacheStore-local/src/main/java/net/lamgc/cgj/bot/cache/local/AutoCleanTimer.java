package net.lamgc.cgj.bot.cache.local;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

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

    private final static AtomicReference<ReferenceQueue<Cleanable>> REFERENCE_QUEUE = new AtomicReference<>(null);

    static {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(new AutoCleanTimer(), 100L, 100L, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(SCHEDULED_EXECUTOR::shutdownNow, "ShutdownThread-AutoClean"));
    }

    /**
     * 增加需要定时执行清理的缓存库.
     * @param store 已实现Cleanable的对象
     */
    public static void add(Cleanable store) {
        CLEANABLE_STORE_SET.add(new WeakReference<>(store, REFERENCE_QUEUE.get()));
    }

    /**
     * 移除对指定 Cleanable 的轮询.
     * @param store 欲停止轮询的 Cleanable 对象.
     */
    public static void remove(final Cleanable store) {
        CLEANABLE_STORE_SET.removeIf(cleanableReference -> cleanableReference.get() == store);
    }

    /**
     * 获取当前被轮询的 Cleanable 数量.
     * @return 返回轮询的 Cleanable 数量.
     */
    public static int size() {
        return CLEANABLE_STORE_SET.size();
    }

    /**
     * 设置虚引用回收队列, 以检查虚引用对象回收状况.
     * <p> 本方法用于诊断 AutoCleanTimer 对虚引用对象的处理情况, 一般情况下无需使用.
     * @param queue 引用队列.
     */
    public static void setWeakReferenceQueue(ReferenceQueue<Cleanable> queue) {
        REFERENCE_QUEUE.set(queue);
    }

    private AutoCleanTimer() {}

    private final Set<WeakReference<Cleanable>> toBeCleanReference = new HashSet<>();

    @Override
    public void run() {
        if (CLEANABLE_STORE_SET.size() == 0) {
            return;
        }

        for (WeakReference<Cleanable> reference : CLEANABLE_STORE_SET) {
            Cleanable store = reference.get();
            if (reference.isEnqueued() || store == null) {
                // 由于 COW ArraySet 的 Iterator 不支持 remove 操作,
                // 所以先收集起来, 等完成所有清理工作后统一删除引用.
                toBeCleanReference.add(reference);
                continue;
            }
            try {
                store.clean();
            } catch (Exception e) {
                log.error("{} 执行清理动作时发生异常:\n{}", store.toString(), Throwables.getStackTraceAsString(e));
            }
        }

        if (toBeCleanReference.size() != 0) {
            CLEANABLE_STORE_SET.removeAll(toBeCleanReference);
            toBeCleanReference.clear();
        }
    }
}
