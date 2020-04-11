package net.lamgc.cgj.bot;

import com.google.common.base.Strings;
import net.lamgc.utils.base.runner.Argument;
import net.lamgc.utils.base.runner.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.Properties;

public class BotAdminCommandProcess {

    private final static Logger log = LoggerFactory.getLogger(BotAdminCommandProcess.class.getSimpleName());

    private final static File globalPropFile = new File("./global.properties");

    @Command
    public String clearCache() {
        BotCommandProcess.clearCache();
        return "操作已完成.";
    }

    @Command
    public String setGlobalProperty(@Argument(name = "key") String key, @Argument(name = "value") String value, @Argument(name = "save", force = false) boolean saveNow) {
        String lastValue = BotCommandProcess.globalProp.getProperty(key);
        BotCommandProcess.globalProp.setProperty(key, Strings.nullToEmpty(value));
        if(saveNow) {
            saveGlobalProperties();
        }
        return "全局配置项 " + key + " 现已设置为: " + value + " (设置前的值: " + lastValue + ")";
    }

    @Command
    public String getGlobalProperty(@Argument(name = "key") String key) {
        return "全局配置项 " + key + " 当前值: " + BotCommandProcess.globalProp.getProperty(key, "(Empty)");
    }

    @Command
    public String saveGlobalProperties() {
        log.info("正在保存全局配置文件...");

        try {
            if(!globalPropFile.exists()) {
                if(!globalPropFile.createNewFile()) {
                    log.error("全局配置项文件保存失败！({})", "文件创建失败");
                    return "全局配置项文件保存失败！";
                }
            }
            BotCommandProcess.globalProp.store(new FileOutputStream(globalPropFile), "");
            log.info("全局配置文件保存成功！");
            return "保存全局配置文件 - 操作已完成.";
        } catch (IOException e) {
            log.error("全局配置项文件保存失败！", e);
            return "全局配置项文件保存失败！";
        }
    }

    @Command
    public String loadGlobalProperties(@Argument(name = "reload", force = false) boolean reload) {
        Properties cache = new Properties();
        if(!globalPropFile.exists()) {
            return "未找到全局配置文件, 无法重载";
        }

        try(Reader reader = new BufferedReader(new FileReader(globalPropFile))) {
            cache.load(reader);
        } catch (IOException e) {
            log.error("重载全局配置文件时发生异常", e);
            return "加载全局配置文件时发生错误!";
        }

        if(reload) {
            BotCommandProcess.globalProp.clear();
        }
        BotCommandProcess.globalProp.putAll(cache);
        return "全局配置文件重载完成.";
    }

    @Command
    public String runUpdateTask(@Argument(force = false, name = "date") Date queryTime) {
        try {
            BotCommandProcess.runUpdateTimer(queryTime);
        } catch (Exception e) {
            log.error("执行更新任务时发生异常", e);
            return "操作执行时发生错误!";
        }
        return "操作已完成.";
    }

}
