package net.lamgc.cgj.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 随机间隔发送器
 */
public class RandomIntervalSendTimer extends TimerTask {

    private final static Timer timer = new Timer("Thread-RIST");
    private final static Logger log = LoggerFactory.getLogger("RandomIntervalSendTimer");
    private final Random timeRandom = new Random();
    private final AutoSender sender;
    private final long time;
    private final int floatTime;
    private AtomicBoolean loop = new AtomicBoolean();
    private final AtomicBoolean start = new AtomicBoolean();


    public RandomIntervalSendTimer(AutoSender sender, long time, int floatTime) {
        this.sender = sender;
        this.time = time;
        this.floatTime = floatTime;
    }

    public void start() {
        start(this.loop.get());
    }

    public void start(boolean loop) {
        this.loop.set(loop);
        long nextDelay = time + timeRandom.nextInt(floatTime);
        log.info("定时器 {} 下一延迟: {}ms", Integer.toHexString(this.hashCode()), nextDelay);
        if(start.get()) {
            try {
                Field state = this.getClass().getSuperclass().getDeclaredField("state");
                state.setAccessible(true);
                state.setInt(this, 0);
                state.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        }
        start.set(true);
        timer.schedule(this, nextDelay);
    }

    @Override
    public void run() {
        sender.send();
        if (this.loop.get()) {
            start();
        }
    }

    @Override
    public boolean cancel() {
        start.set(false);
        return super.cancel();
    }

}
