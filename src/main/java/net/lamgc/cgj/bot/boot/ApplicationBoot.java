package net.lamgc.cgj.bot.boot;

import net.lamgc.cgj.bot.event.BotEventHandler;
import net.lamgc.cgj.util.PropertiesUtils;
import net.lamgc.utils.base.ArgumentsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApplicationBoot {

    private final static Logger log = LoggerFactory.getLogger(ApplicationBoot.class);

    private ApplicationBoot() {}

    /**
     * 初始化应用.
     * <p>该方法不会初始化机器人, 仅初始化应用所需的配置信息.</p>
     */
    public static void initialApplication(String[] args) {
        ArgumentsProperties argsProp = new ArgumentsProperties(args);
        if(!PropertiesUtils.getSettingToSysProp(argsProp, "proxy", null)) {
            PropertiesUtils.getEnvSettingToSysProp("CGJ_PROXY", "proxy", null);
        }
        if(!PropertiesUtils.getSettingToSysProp(argsProp, "botDataDir", "./") &&
                !PropertiesUtils.getEnvSettingToSysProp("CGJ_BOT_DATA_DIR", "botDataDir", "./")) {
            log.warn("未设置botDataDir, 当前运行目录将作为酷Q机器人所在目录.");
        }
        if(!PropertiesUtils.getSettingToSysProp(argsProp, "redisAddress", "127.0.0.1") &&
                !PropertiesUtils.getEnvSettingToSysProp("CGJ_REDIS_URI", "redisAddress", "127.0.0.1")) {
            log.warn("未设置RedisAddress, 将使用默认值连接Redis服务器(127.0.0.1:6379)");
        }

        // 初始化 BotGlobal
        //noinspection ResultOfMethodCallIgnored 这里仅仅是加载BotGlobal而已
        BotGlobal.getGlobal();
    }

    /**
     * 初始化机器人.
     * <p>本方法由框架调用.</p>
     */
    public static void initialBot() {
        BotEventHandler.initial();
    }

}
