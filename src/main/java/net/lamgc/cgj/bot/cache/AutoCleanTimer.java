package net.lamgc.cgj.bot.cache;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

public class AutoCleanTimer extends TimerTask {

    private final static Set<Cleanable> cleanSet = new CopyOnWriteArraySet<>();

    private final static Timer cleanTimer = new Timer("Thread-AutoClean", true);

    private final static Logger log = LoggerFactory.getLogger(AutoCleanTimer.class);

    static {
        cleanTimer.schedule(new AutoCleanTimer(), 100L);
    }

    /**
     * 增加需要定时执行清理的缓存库
     * @param store 已实现Cleanable的对象
     */
    public static void add(Cleanable store) {
        cleanSet.add(store);
    }

    /**
     * 移除已添加的缓存库
     * @param store 需要从AutoCleanTimer移除的对象
     */
    public static void remove(Cleanable store) {
        cleanSet.remove(store);
    }

    private AutoCleanTimer() {}

    @Override
    public void run() {
        cleanSet.forEach(cleanable -> {
            try {
                cleanable.clean();
            } catch (Exception e) {
                log.error("{} 执行清理动作时发生异常:\n{}", cleanable.toString(), Throwables.getStackTraceAsString(e));
            }
        });
    }
}
