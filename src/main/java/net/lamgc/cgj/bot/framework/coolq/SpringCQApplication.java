package net.lamgc.cgj.bot.framework.coolq;

import net.lamgc.cgj.Main;
import net.lamgc.cgj.bot.boot.BotGlobal;
import net.lamgc.cgj.bot.framework.Framework;
import net.lamgc.cgj.bot.framework.FrameworkManager;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;

public class SpringCQApplication implements Framework {

    private Logger log;

    private final Object quitLock = new Object();

    @Override
    public void init(FrameworkManager.FrameworkResources resources) {
        this.log = resources.getLogger();
    }

    public void run() {
        log.info("酷Q机器人根目录: {}", BotGlobal.getGlobal().getDataStoreDir().getPath());
        ConfigurableApplicationContext context = SpringApplication.run(Main.class);
        registerShutdownHook(context);
        try {
            synchronized (quitLock) {
                quitLock.wait();
            }
        } catch (InterruptedException e) {
            log.warn("发生中断, 退出SpringCQ...", e);
        }

        context.stop();
        context.close();
    }

    private void registerShutdownHook(ConfigurableApplicationContext context) {
        context.addApplicationListener((ApplicationListener<ApplicationFailedEvent>)
                event -> close());
        context.addApplicationListener((ApplicationListener<ContextClosedEvent>)
                event -> close());
        context.addApplicationListener((ApplicationListener<ContextStoppedEvent>)
                event -> close());
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public void close() {
        synchronized (quitLock) {
            quitLock.notify();
        }
    }

}
