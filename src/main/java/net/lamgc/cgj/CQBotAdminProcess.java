package net.lamgc.cgj;

import com.google.common.base.Strings;
import net.lamgc.utils.base.runner.Argument;
import net.lamgc.utils.base.runner.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CQBotAdminProcess {

    private final static Logger log = LoggerFactory.getLogger("CQBotAdminProcess");

    @Command
    public String clearCache() {
        CQProcess.clearCache();
        return "操作已完成.";
    }

    @Command
    public String setGlobalProperty(@Argument(name = "key") String key, @Argument(name = "value") String value, @Argument(name = "save", force = false) boolean saveNow) {
        String lastValue = CQPluginMain.globalProp.getProperty(key);
        CQPluginMain.globalProp.setProperty(key, Strings.nullToEmpty(value));
        if(saveNow) {
            saveGlobalProperties();
        }
        return "全局配置项 " + key + " 现已设置为: " + value + " (设置前的值: " + lastValue + ")";
    }

    @Command
    public String getGlobalProperty(@Argument(name = "key") String key) {
        return "全局配置项 " + key + " 当前值: " + CQPluginMain.globalProp.getProperty(key, "(Empty)");
    }

    @Command
    public String saveGlobalProperties() {
        log.info("正在保存全局配置文件...");
        File globalPropFile = new File("./global.properties");
        try {
            if(!globalPropFile.exists()) {
                if(!globalPropFile.createNewFile()) {
                    log.error("全局配置项文件保存失败！({})", "文件创建失败");
                    return "全局配置项文件保存失败！";
                }
            }
            CQPluginMain.globalProp.store(new FileOutputStream(globalPropFile), "");
            log.info("全局配置文件保存成功！");
            return "保存全局配置文件 - 操作已完成.";
        } catch (IOException e) {
            log.error("全局配置项文件保存失败！", e);
            return "全局配置项文件保存失败！";
        }
    }

}
