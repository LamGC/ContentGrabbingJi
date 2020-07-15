package net.lamgc.cgj.bot.framework;

import org.slf4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

public final class FrameworkManager {

    private FrameworkManager() {}

    private final static Map<Framework, FrameworkResources> resourcesMap = new HashMap<>();

    private final static ThreadGroup frameworkRootGroup = new ThreadGroup("FrameworkRootGroup");

    static {
        Runtime.getRuntime()
                .addShutdownHook(new Thread(FrameworkManager::shutdownAllFramework, "FrameworkManager-Shutdown"));
    }

    public static Thread registerFramework(Framework framework) {
        checkFramework(framework);
        FrameworkResources resources = new FrameworkResources(framework);
        resourcesMap.put(framework, resources);
        Thread frameworkThread = new Thread(resources.getFrameworkThreadGroup(),
                () -> FrameworkManager.runFramework(framework), "FrameworkThread-" + framework.getIdentify());

        frameworkThread.start();
        return frameworkThread;
    }

    private static final Pattern FRAMEWORK_NAME_CHECK_PATTERN = Pattern.compile("^[A-Za-z0-9_\\-$]+$");
    private static void checkFramework(Framework framework) {
        if(!FRAMEWORK_NAME_CHECK_PATTERN.matcher(framework.getFrameworkName()).matches()) {
            throw new IllegalStateException("Invalid Framework Name: " + framework.getFrameworkName());
        }
    }

    public static Set<Framework> frameworkSet() {
        return new HashSet<>(resourcesMap.keySet());
    }

    public static void shutdownAllFramework() {
        for (Framework framework : resourcesMap.keySet()) {
            FrameworkResources frameworkResources = resourcesMap.get(framework);
            Logger frameworkLogger = frameworkResources.getLogger();
            try {
                frameworkLogger.info("正在关闭框架...");
                framework.close();
                frameworkLogger.info("框架已关闭.");
                frameworkResources.getFrameworkThreadGroup().interrupt();
                resourcesMap.remove(framework);
            } catch(Throwable e) {
                frameworkLogger.error("退出框架时发生异常", e);
            }
        }
    }

    static ThreadGroup getFrameworkRootGroup() {
        return frameworkRootGroup;
    }

    private static void runFramework(Framework framework) {
        FrameworkResources frameworkResources = resourcesMap.get(framework);
        try {
            framework.init(frameworkResources);
            framework.run();
        } catch(Throwable e) {
            frameworkResources.getLogger().error("框架未捕获异常, 导致异常退出.", e);
        } finally {
            frameworkResources.getFrameworkThreadGroup().interrupt();
        }
    }

}
