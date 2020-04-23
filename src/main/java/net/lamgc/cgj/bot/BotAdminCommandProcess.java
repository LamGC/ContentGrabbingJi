package net.lamgc.cgj.bot;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.lamgc.cgj.bot.message.MessageSenderBuilder;
import net.lamgc.cgj.bot.message.MessageSource;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.utils.base.runner.Argument;
import net.lamgc.utils.base.runner.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class BotAdminCommandProcess {

    private final static Logger log = LoggerFactory.getLogger(BotAdminCommandProcess.class.getSimpleName());

    private final static File globalPropFile = new File("global.properties");

    private final static File pushListFile = new File("pushList.json");

    private final static Hashtable<Long, JsonObject> pushInfoMap = new Hashtable<>();

    private final static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    @Command
    public static String cleanCache() {
        BotCommandProcess.clearCache();
        return "操作已完成.";
    }

    @Command
    public static String setGlobalProperty(@Argument(name = "key") String key, @Argument(name = "value") String value, @Argument(name = "save", force = false) boolean saveNow) {
        String lastValue = BotCommandProcess.globalProp.getProperty(key);
        BotCommandProcess.globalProp.setProperty(key, Strings.nullToEmpty(value));
        if(saveNow) {
            saveGlobalProperties();
        }
        return "全局配置项 " + key + " 现已设置为: " + value + " (设置前的值: " + lastValue + ")";
    }

    @Command
    public static String getGlobalProperty(@Argument(name = "key") String key) {
        return "全局配置项 " + key + " 当前值: " + BotCommandProcess.globalProp.getProperty(key, "(Empty)");
    }

    @Command
    public static String saveGlobalProperties() {
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
    public static String loadGlobalProperties(@Argument(name = "reload", force = false) boolean reload) {
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
    public static String runUpdateTask(@Argument(force = false, name = "date") Date queryTime) {
        try {
            BotCommandProcess.runUpdateTimer(queryTime);
        } catch (Exception e) {
            log.error("执行更新任务时发生异常", e);
            return "操作执行时发生错误!";
        }
        return "操作已完成.";
    }

    @Command
    public static String addPushGroup(@Argument(name = "group") long groupId,
                                      @Argument(name = "minTime", force = false, defaultValue = "21600000") long minTime,
                                      @Argument(name = "floatTime", force = false, defaultValue = "10800000") int floatTime,
                                      @Argument(name = "rankingStart", force = false, defaultValue = "1") int rankingStart,
                                      @Argument(name = "rankingStop", force = false, defaultValue = "150") int rankingStop,
                                      @Argument(name = "original", force = false, defaultValue = "false") boolean original
    ) {
        JsonObject setting = new JsonObject();
        setting.addProperty("time.min", minTime);
        setting.addProperty("time.float", floatTime);
        setting.addProperty("ranking.start", rankingStart);
        setting.addProperty("ranking.end", rankingStop);
        setting.addProperty("pageQuality.original", original);
        if(pushInfoMap.containsKey(groupId)) {
            log.info("群 {} 已存在Timer, 删除Timer...", groupId);
            removePushTimer(groupId);
        }

        log.info("正在增加Timer...(Setting: {})", setting);
        pushInfoMap.put(groupId, setting);
        addPushTimer(groupId, setting);
        return "已在 " + groupId + " 开启定时推送功能。";
    }

    /**
     * 重载推送列表
     */
    @Command
    public static String loadPushList() {
        pushInfoMap.clear();
        if(!pushListFile.exists()) {
            log.warn("推送列表文件不存在, 跳过加载.");
            return "文件不存在, 跳过加载.";
        }

        try (Reader reader = new BufferedReader(new FileReader(pushListFile))) {
            pushInfoMap.putAll(gson.fromJson(reader, new TypeToken<Map<Long, JsonObject>>(){}.getType()));
            loadAllPushTimer(false);
            return "列表重载完成";
        } catch (IOException e) {
            log.error("重载推送列表时发生错误", e);
            return "加载时发生异常";
        }
    }

    @Command
    public static String savePushList() {
        try {
            if(!pushListFile.exists()) {
                pushListFile.createNewFile();
            }
        } catch (IOException e) {
            log.error("PushList.json文件创建失败", e);
            return "保存失败!请检查控制台信息.";
        }

        try (Writer writer = new FileWriter(pushListFile)) {
            writer.write(gson.toJson(pushInfoMap));
            return "保存成功.";
        } catch (IOException e) {
            log.error("写入PushList.json文件失败!", e);
            return "保存失败!请检查控制台信息.";
        }
    }

    public static void loadAllPushTimer(boolean flush) {
        if(flush) {
            RandomIntervalSendTimer.timerIdSet().forEach(id -> RandomIntervalSendTimer.getTimerById(id).destroy());
        }
        cleanPushTimer();
        pushInfoMap.forEach(BotAdminCommandProcess::addPushTimer);
    }

    private static void addPushTimer(long id, JsonObject setting) {
        try {
            RandomIntervalSendTimer.getTimerById(id);
            return;
        } catch(NoSuchElementException ignored) {
        }

        AutoSender sender = new RandomRankingArtworksSender(
                MessageSenderBuilder.getMessageSender(MessageSource.Group, id),
                setting.get("ranking.start").getAsInt(),
                setting.get("ranking.end").getAsInt(),
                setting.get("pageQuality.original").getAsBoolean() ? PixivDownload.PageQuality.ORIGINAL : PixivDownload.PageQuality.REGULAR
        );

        RandomIntervalSendTimer.createTimer(
                id,
                sender,
                setting.get("time.min").getAsLong(),
                setting.get("time.float").getAsInt(),
                true, true);
    }

    /**
     * 删除一个推送定时器
     * @param id 群号
     * @throws NoSuchElementException 当这个群号没有定时器的时候抛出异常
     */
    @Command
    public static void removePushTimer(@Argument(name = "group") long id) {
        RandomIntervalSendTimer.getTimerById(id).destroy();
        pushInfoMap.remove(id);
    }

    /**
     * 根据已修改的pushInfoMap将已经被删除的Timer取消
     */
    private static void cleanPushTimer() {
        RandomIntervalSendTimer.timerIdSet().forEach(id -> {
            if(!pushInfoMap.containsKey(id)) {
                RandomIntervalSendTimer.getTimerById(id).destroy();
            }
        });
    }

}
