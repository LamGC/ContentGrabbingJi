package net.lamgc.cgj.util;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TimeLimitThreadPoolExecutorTest {

    private final static Logger log = LoggerFactory.getLogger(TimeLimitThreadPoolExecutorTest.class);

    @Test
    public void timeoutTest() throws InterruptedException {
        TimeLimitThreadPoolExecutor executor = new TimeLimitThreadPoolExecutor(1000, 1, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));
        log.info("ThreadPoolExecutor.isTerminated: {}", executor.isTerminated());
        log.info("ThreadPoolExecutor.isShutdown: {}", executor.isShutdown());

        executor.setTimeoutCheckInterval(150);
        log.info("当前设定: ExecuteTimeLimit: {}ms, CheckInterval: {}ms", executor.getExecuteTimeLimit(),
                executor.getTimeoutCheckInterval());
        executor.execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("线程 " + Thread.currentThread().getName() + " 被中断");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Assert.fail("Multiple interrupts occurred");
            }
        });
        executor.shutdown();
        Assert.assertTrue(executor.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS));
    }

}
