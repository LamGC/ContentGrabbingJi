package net.lamgc.cgj.bot.framework.coolq;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.lz1998.cq.CQGlobal;
import net.lz1998.cq.EnableCQ;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableCQ
public class CQConfig {

    public static void init() {
        CQGlobal.pluginList.add(CQPluginMain.class);
        CQGlobal.executor = new ThreadPoolExecutor(
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                Runtime.getRuntime().availableProcessors(),
                25, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(512),
                new ThreadFactoryBuilder()
                        .setNameFormat("Plugin-ProcessThread-%d")
                        .build()
        );
    }

}
