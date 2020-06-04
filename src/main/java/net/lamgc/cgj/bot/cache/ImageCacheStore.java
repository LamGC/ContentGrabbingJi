package net.lamgc.cgj.bot.cache;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lamgc.cgj.bot.cache.exception.HttpRequestException;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public final class ImageCacheStore {

    private final static Logger log = LoggerFactory.getLogger(ImageCacheStore.class.getName());

    private final static Map<ImageCacheObject, Task> cacheMap = new Hashtable<>();

    private final static ThreadPoolExecutor imageCacheExecutor = new ThreadPoolExecutor(
            4, 6,
            30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactoryBuilder()
                    .setNameFormat("ImageCacheThread-%d")
                    .build()
    );

    private final static ImageCacheHandler handler = new ImageCacheHandler();

    static {
        Thread shutdownThread = new Thread(imageCacheExecutor::shutdownNow);
        shutdownThread.setName("Thread-ImageCacheShutdown");
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    private ImageCacheStore() {}

    /**
     * 传递图片缓存任务, 并等待缓存完成.
     * @param cacheObject 缓存任务组
     */
    public static Throwable executeCacheRequest(ImageCacheObject cacheObject) throws InterruptedException {
        Task task = getTaskState(cacheObject);
        if(task.taskState.get() == TaskState.COMPLETE) {
            return null;
        }

        boolean locked = false;
        try {
            if(task.taskState.get() == TaskState.COMPLETE) {
                return null;
            }
            task.lock.lock();
            locked = true;
            // 双重检查
            if(task.taskState.get() == TaskState.COMPLETE) {
                return null;
            }

            // 置任务状态
            task.taskState.set(TaskState.RUNNING);

            Throwable throwable = null;
            try {
                throwable = imageCacheExecutor.submit(() -> {
                    try {
                        handler.getImageToCache(cacheObject);
                    } catch (Throwable e) {
                        return e;
                    }
                    return null;
                }).get();

                if(throwable == null) {
                    task.taskState.set(TaskState.COMPLETE);
                } else {
                    task.taskState.set(TaskState.ERROR);
                }
            } catch (ExecutionException e) {
                log.error("执行图片缓存任务时发生异常", e);
            }
            return throwable;
        } finally {
            if(locked) {
                task.lock.unlock();
            }
        }

    }

    private static Task getTaskState(ImageCacheObject cacheObject) {
        if(!cacheMap.containsKey(cacheObject)) {
            cacheMap.put(cacheObject, new Task());
        }
        return cacheMap.get(cacheObject);
    }

    /**
     * 获取错误信息
     */
    public static String getErrorMessageFromThrowable(Throwable throwable, boolean onlyRequestException) {
        if(throwable == null) {
            return "";
        } else if(!(throwable instanceof HttpRequestException)) {
            if(onlyRequestException) {
                return "";
            }
            return throwable.getMessage();
        }
        JsonObject result = new Gson()
            .fromJson(((HttpRequestException) throwable).getContent(), JsonObject.class);
        return result.get("msg").getAsString();
    }

    /**
     * 任务状态
     */
    private enum TaskState {
        READY, RUNNING, COMPLETE, ERROR
    }

    private static class Task {

        public final ReentrantLock lock = new ReentrantLock(true);

        public final AtomicReference<TaskState> taskState = new AtomicReference<>(TaskState.READY);

    }

}
