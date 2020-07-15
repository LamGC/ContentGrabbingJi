package net.lamgc.cgj.bot.framework;

import net.lamgc.cgj.bot.boot.BotGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FrameworkResources {

    private final static File frameworkDataStoreRootDir = new File(BotGlobal.getGlobal().getDataStoreDir(),
            "frameworks/");

    private final ThreadGroup frameworkThreadGroup;

    private final Logger logger;

    private final File frameworkDataStoreDir;

    public FrameworkResources(Framework framework) {
        frameworkThreadGroup = new ThreadGroup(FrameworkManager.getFrameworkRootGroup(),
                "Framework-" + framework.getIdentify());
        frameworkDataStoreDir = new File(frameworkDataStoreRootDir, framework.getClass().getSimpleName());
        logger = LoggerFactory.getLogger("Framework-" + framework.getIdentify());
    }

    ThreadGroup getFrameworkThreadGroup() {
        return frameworkThreadGroup;
    }

    public Logger getLogger() {
        return logger;
    }

}
