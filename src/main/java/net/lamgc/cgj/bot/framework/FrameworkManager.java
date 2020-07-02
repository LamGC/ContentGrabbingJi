package net.lamgc.cgj.bot.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class FrameworkManager {

    private FrameworkManager() {}

    private final static Map<Framework, FrameworkResources> resourcesMap = new HashMap<>();

    private final static ThreadGroup frameworkRootGroup = new ThreadGroup("FrameworkRootGroup");

    static {
        Runtime.getRuntime()
                .addShutdownHook(new Thread(FrameworkManager::shutdownAllFramework, "FrameworkManager-Shutdown"));
    }

    public static void registerFramework(Framework framework) {
        FrameworkResources resources = new FrameworkResources(framework);
        resourcesMap.put(framework, resources);
        new Thread(resources.getFrameworkThreadGroup(),
                () -> FrameworkManager.runFramework(framework), "FrameworkThread-" + framework.getName()).start();
    }

    public static void shutdownAllFramework() {
        for (Framework framework : resourcesMap.keySet()) {
            Logger frameworkLogger = resourcesMap.get(framework).getLogger();
            try {
                frameworkLogger.info("正在关闭框架...");
                framework.close();
                frameworkLogger.info("框架已关闭.");
                resourcesMap.remove(framework);
            } catch(Throwable e) {
                frameworkLogger.error("退出框架时发生异常", e);
            }
        }
        frameworkRootGroup.interrupt();
    }

    private static void runFramework(Framework framework) {
        FrameworkResources frameworkResources = resourcesMap.get(framework);
        try {
            framework.init(frameworkResources);
            framework.run();
            frameworkResources.getFrameworkThreadGroup().interrupt();
        } catch(Throwable e) {
            frameworkResources.getLogger().error("框架未捕获异常, 导致异常退出.", e);
        }
    }

    public static class FrameworkResources {

        private final ThreadGroup frameworkThreadGroup;

        private final Logger logger;

        public FrameworkResources(Framework framework) {
            frameworkThreadGroup = new ThreadGroup(frameworkRootGroup, "Framework-" + framework.getName());
            logger = LoggerFactory.getLogger("Framework-" + framework.getName());
        }

        ThreadGroup getFrameworkThreadGroup() {
            return frameworkThreadGroup;
        }

        public Logger getLogger() {
            return logger;
        }
    }

}
