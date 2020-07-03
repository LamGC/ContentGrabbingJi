package net.lamgc.cgj.bot.framework.coolq;

import com.google.common.base.Strings;
import net.lamgc.cgj.bot.boot.BotGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class SpringCQApplication {

    private final static Logger log = LoggerFactory.getLogger(SpringCQApplication.class);

    private final Object quitLock = new Object();

    public void start(String argsStr) {
        log.info("酷Q机器人根目录: {}", BotGlobal.getGlobal().getDataStoreDir().getPath());
        Pattern pattern = Pattern.compile("/\\s*(\".+?\"|[^:\\s])+((\\s*:\\s*(\".+?\"|[^\\s])+)|)|(\".+?\"|[^\"\\s])+");
        Matcher matcher = pattern.matcher(Strings.nullToEmpty(argsStr));
        ArrayList<String> argsList = new ArrayList<>();
        while (matcher.find()) {
            argsList.add(matcher.group());
        }
        String[] args = new String[argsList.size()];
        argsList.toArray(args);
        ConfigurableApplicationContext context = SpringApplication.run(SpringCQApplication.class, args);
        registerShutdownHook(context);
        try {
            synchronized (quitLock) {
                quitLock.wait();
            }
        } catch (InterruptedException e) {
            log.warn("发生中断, 退出SpringCQ...", e);
        }
    }

    private void registerShutdownHook(ConfigurableApplicationContext context) {
        context.addApplicationListener((ApplicationListener<ApplicationFailedEvent>)
                event -> notifyThread());
        context.addApplicationListener((ApplicationListener<ContextClosedEvent>)
                event -> notifyThread());
        context.addApplicationListener((ApplicationListener<ContextStoppedEvent>)
                event -> notifyThread());
        Runtime.getRuntime().addShutdownHook(new Thread(this::notifyThread));
    }

    private void notifyThread() {
        synchronized (quitLock) {
            quitLock.notify();
        }
    }

}
