package net.lamgc.cgj.util;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 带有时间限制的线程池.
 * 当线程超出了限制时间时, 将会对该线程发出中断.
 */
public class TimeLimitThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 执行时间限制, 单位毫秒.
     * 默认30s.
     */
    private final AtomicLong executeTimeLimit = new AtomicLong();

    /**
     * 检查间隔时间.
     * 默认100ms.
     */
    private final AtomicLong timeoutCheckInterval = new AtomicLong(100);

    private final Map<Thread, AtomicLong> workerThreadMap = new Hashtable<>();

    private final Thread timeoutCheckThread = createTimeoutCheckThread();

    public TimeLimitThreadPoolExecutor(long executeLimitTime, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        setInitialTime(0, executeLimitTime);
        timeoutCheckThread.start();
    }

    public TimeLimitThreadPoolExecutor(long executeLimitTime, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        setInitialTime(0, executeLimitTime);
        timeoutCheckThread.start();
    }

    public TimeLimitThreadPoolExecutor(long executeLimitTime, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        setInitialTime(0, executeLimitTime);
        timeoutCheckThread.start();
    }

    public TimeLimitThreadPoolExecutor(long executeLimitTime, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        setInitialTime(0, executeLimitTime);
        timeoutCheckThread.start();
    }

    private void setInitialTime(long checkInterval, long executeLimitTime) {
        if(checkInterval > 0) {
            timeoutCheckInterval.set(checkInterval);
        }
        if(executeLimitTime > 0) {
            executeTimeLimit.set(executeLimitTime);
        }
    }

    /**
     * 设置执行时间.
     * <p>注意: 该修改仅在线程池完全停止后才有效</p>
     * @see #isTerminated()
     * @param time 新的限制时间(ms)
     */
    public void setExecuteTimeLimit(long time) {
        if(time <= 0) {
            throw new IllegalArgumentException("Time is not allowed to be set to 0 or less");
        }
        if(this.isTerminated()) {
            executeTimeLimit.set(time);
        }
    }

    /**
     * 设置超时检查间隔.
     * <p>该方法仅会在当前检查后生效.</p>
     * @param time 新的检查间隔(ms)
     */
    public void setTimeoutCheckInterval(long time) {
        if(time <= 0) {
            throw new IllegalArgumentException("Time is not allowed to be set to 0 or less");
        }
        timeoutCheckInterval.set(time);
    }

    /**
     * 获取当前设置的执行时间限制.
     * @return 执行时间限制(ms).
     */
    public long getExecuteTimeLimit() {
        return executeTimeLimit.get();
    }

    /**
     * 获取当前设定的超时检查间隔
     * @return 间隔时间(ms).
     */
    public long getTimeoutCheckInterval() {
        return timeoutCheckInterval.get();
    }

    private Thread createTimeoutCheckThread() {
        Thread checkThread = new Thread(() -> {
            if(executeTimeLimit.get() <= 0) {
                return;
            }
            while (true) {
                try {
                    long interval = this.timeoutCheckInterval.get();
                    Thread.sleep(interval);

                    // 检查是否存在超时的任务
                    workerThreadMap.forEach((thread, time) -> {
                        long currentTime = time.getAndAdd(interval);
                        if(currentTime > executeTimeLimit.get()) {
                            if(!thread.isInterrupted()) {
                                thread.interrupt();
                            }
                        }
                    });
                } catch(InterruptedException ignored) {
                    break;
                }
            }
        });

        checkThread.setName("ThreadPool-" + Integer.toHexString(this.hashCode()) +"-TimeoutCheck");
        return checkThread;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        workerThreadMap.put(t, new AtomicLong());
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        workerThreadMap.remove(Thread.currentThread());
        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
        this.timeoutCheckThread.interrupt();
        super.terminated();
    }
}
