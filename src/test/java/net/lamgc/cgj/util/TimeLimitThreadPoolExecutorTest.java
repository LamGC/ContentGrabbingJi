package net.lamgc.cgj.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TimeLimitThreadPoolExecutorTest {

    @Test
    public void timeoutTest() throws InterruptedException {
        TimeLimitThreadPoolExecutor executor = new TimeLimitThreadPoolExecutor(1000, 1, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));
        System.out.println(executor.isTerminated());
        System.out.println(executor.isShutdown());

        executor.setTimeoutCheckInterval(150);
        System.out.println("当前设定: ETL: " + executor.getExecuteTimeLimit() + "ms, TCI: " + executor.getTimeoutCheckInterval() + "ms");
        executor.execute(() -> {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                System.out.println("线程 " + Thread.currentThread().getName() + " 被中断");
            }
        });
        executor.shutdown();
        Assert.assertTrue(executor.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS));
    }

}
