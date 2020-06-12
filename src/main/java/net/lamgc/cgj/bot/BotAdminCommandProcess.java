package net.lamgc.cgj.bot;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.lamgc.cgj.bot.boot.BotGlobal;
import net.lamgc.cgj.bot.message.MessageSenderBuilder;
import net.lamgc.cgj.bot.message.MessageSource;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivURL;
import net.lamgc.utils.base.runner.Argument;
import net.lamgc.utils.base.runner.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class BotAdminCommandProcess {

    private final static Logger log = LoggerFactory.getLogger(BotAdminCommandProcess.class);

    private final static File pushListFile = new File(BotGlobal.getGlobal().getDataStoreDir(), "pushList.json");

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
    public static String setProperty(
            @Argument(name = "group", force = false) long groupId,
            @Argument(name = "key") String key,
            @Argument(name = "value") String value
    ) {
        if(Strings.isNullOrEmpty(key)) {
            return "未选择配置项key.";
        }
        String lastValue = SettingProperties.setProperty(groupId, key, value.equals("null") ? null : value);
        return (groupId <= 0 ? "已更改全局配置 " : "已更改群组 " + groupId + " 配置 ") +
                key + " 的值: '" + value + "' (原配置值: '" + lastValue + "')";
    }

    @Command
    public static String getProperty(
            @Argument(name = "group", force = false) long groupId,
            @Argument(name = "key") String key
    ) {
        if(Strings.isNullOrEmpty(key)) {
            return "未选择配置项key.";
        }
        return (groupId <= 0 ? "全局配置 " : "群组 " + groupId + " 配置 ") +
                key + " 设定值: '" + SettingProperties.getProperty(groupId, key, "(empty)") + "'";
    }

    @Command
    public static String saveProperties() {
        log.info("正在保存配置文件...");
        SettingProperties.saveProperties();
        log.info("配置文件保存操作已完成.");
        return "保存配置 - 操作已完成.";
    }

    @Command
    public static String loadProperties(@Argument(name = "reload", force = false) boolean reload) {
        if(reload) {
            SettingProperties.clearProperties();
        }

        SettingProperties.loadProperties();
        return "操作已完成.";
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

    private final static String RANKING_SETTING_TIME_MIN = "time.min";
    private final static String RANKING_SETTING_TIME_FLOAT = "time.float";
    private final static String RANKING_SETTING_RANKING_START = "ranking.start";
    private final static String RANKING_SETTING_RANKING_END = "ranking.end";
    private final static String RANKING_SETTING_RANKING_MODE = "ranking.mode";
    private final static String RANKING_SETTING_RANKING_CONTENT_TYPE = "ranking.contentType";
    private final static String RANKING_SETTING_PAGE_QUALITY = "page.quality";

    @Command
    public static String addPushGroup(
            @Argument(name = "$fromGroup") long fromGroup,
            @Argument(name = "group", force = false, defaultValue = "0") long groupId,
            @Argument(name = "minTime", force = false, defaultValue = "21600000") long minTime,
            @Argument(name = "floatTime", force = false, defaultValue = "10800000") int floatTime,
            @Argument(name = "rankingStart", force = false, defaultValue = "1") int rankingStart,
            @Argument(name = "rankingStop", force = false, defaultValue = "150") int rankingStop,
            @Argument(name = "mode", force = false, defaultValue = "DAILY") String rankingMode,
            @Argument(name = "type", force = false, defaultValue = "ILLUST") String rankingContentType,
            @Argument(name = "original", force = false, defaultValue = "false") boolean original
    ) {
        if(minTime <= 0 || floatTime <= 0) {
            return "时间不能为0或负数！";
        } else if(rankingStart <= 0 || rankingStop - rankingStart <= 0) {
            return "排行榜范围选取错误！";
        }

        PixivURL.RankingContentType type;
        PixivURL.RankingMode mode;
        try {
            type = PixivURL.RankingContentType.valueOf("TYPE_" + rankingContentType.toUpperCase());
        } catch(IllegalArgumentException e) {
            return "无效的排行榜类型参数！";
        }

        try {
            mode = PixivURL.RankingMode.valueOf("MODE_" + rankingMode.toUpperCase());
        } catch(IllegalArgumentException e) {
            return "无效的排行榜模式参数！";
        }

        if(!type.isSupportedMode(mode)) {
            return "不兼容的排行榜模式与类型！";
        }

        long group = groupId <= 0 ? fromGroup : groupId;
        JsonObject setting = new JsonObject();
        setting.addProperty(RANKING_SETTING_TIME_MIN, minTime);
        setting.addProperty(RANKING_SETTING_TIME_FLOAT, floatTime);
        setting.addProperty(RANKING_SETTING_RANKING_START, rankingStart);
        setting.addProperty(RANKING_SETTING_RANKING_END, rankingStop);
        setting.addProperty(RANKING_SETTING_RANKING_MODE, rankingMode);
        setting.addProperty(RANKING_SETTING_RANKING_CONTENT_TYPE, rankingContentType);
        setting.addProperty(RANKING_SETTING_PAGE_QUALITY, original ?
                PixivDownload.PageQuality.ORIGINAL.name() :
                PixivDownload.PageQuality.REGULAR.name());
        if(pushInfoMap.containsKey(group)) {
            log.info("群 {} 已存在Timer, 删除Timer...", group);
            removePushGroup(fromGroup, groupId);
        }

        log.info("群组 {} 新推送配置: {}", group, setting);
        log.info("正在增加Timer...");
        pushInfoMap.put(group, setting);
        addPushTimer(group, setting);
        return "已在 " + group + " 开启定时推送功能。";
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
            if(!pushListFile.exists() && !pushListFile.createNewFile()) {
                throw new IOException("文件夹创建失败!(Path: " + pushListFile.getPath() + ")");
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

    /**
     * 加载所有推送Timer
     * @param flush 是否完全重载, 如为true则加载前会删除所有已加载的Timer
     */
    public static void loadAllPushTimer(boolean flush) {
        if(flush) {
            RandomIntervalSendTimer.timerIdSet().forEach(id -> RandomIntervalSendTimer.getTimerById(id).destroy());
        } else {
            cleanPushTimer();
        }
        pushInfoMap.forEach(BotAdminCommandProcess::addPushTimer);
    }

    /**
     * 根据设置增加Timer
     * @param id 群组id
     * @param setting jsonObject设置集
     */
    private static void addPushTimer(long id, JsonObject setting) {
        try {
            RandomIntervalSendTimer.getTimerById(id);
            return;
        } catch(NoSuchElementException ignored) {
        }

        int rankingStart = setting.has(RANKING_SETTING_RANKING_START) ? setting.get(RANKING_SETTING_RANKING_START).getAsInt() : 1;
        int rankingEnd = setting.has(RANKING_SETTING_RANKING_END) ? setting.get(RANKING_SETTING_RANKING_END).getAsInt() : 150;
        PixivURL.RankingMode rankingMode = PixivURL.RankingMode.MODE_DAILY;
        PixivURL.RankingContentType rankingContentType = PixivURL.RankingContentType.TYPE_ILLUST;
        PixivDownload.PageQuality pageQuality = PixivDownload.PageQuality.REGULAR;

        if(rankingStart <= 0 || rankingStart > 500) {
            log.warn("群组 [{}] - 无效的RankingStart设定值, 将重置为默认设定值(1): {}", id, rankingStart);
            rankingStart = 1;
        } else if(rankingEnd > 500 || rankingEnd <= 0) {
            log.warn("群组 [{}] - 无效的RankingEnd设定值, 将重置为默认设定值(150): {}", id, rankingEnd);
            rankingEnd = 150;
        } else if(rankingStart > rankingEnd) {
            log.warn("群组 [{}] - 无效的排行榜选取范围, 将重置为默认设定值(1 ~ 150): start={}, end={}", id, rankingStart, rankingEnd);
            rankingStart = 1;
            rankingEnd = 150;
        }

        if(setting.has(RANKING_SETTING_RANKING_MODE)) {
            String value = setting.get(RANKING_SETTING_RANKING_MODE).getAsString().trim().toUpperCase();
            try {
                rankingMode = PixivURL.RankingMode.valueOf(value.startsWith("MODE_") ? value : "MODE_" + value);
            } catch(IllegalArgumentException e) {
                log.warn("群组ID [{}] - 无效的RankingMode设定值, 将重置为默认值: {}", id, value);
            }
        }
        if(setting.has(RANKING_SETTING_RANKING_CONTENT_TYPE)) {
            String value = setting.get(RANKING_SETTING_RANKING_CONTENT_TYPE).getAsString().trim().toUpperCase();
            try {
                rankingContentType = PixivURL.RankingContentType.valueOf(value.startsWith("TYPE_") ? value : "TYPE_" + value);
            } catch(IllegalArgumentException e) {
                log.warn("群组ID [{}] - 无效的RankingContentType设定值: {}", id, value);
            }
        }

        if(setting.has(RANKING_SETTING_PAGE_QUALITY)) {
            String value = setting.get(RANKING_SETTING_PAGE_QUALITY).getAsString().trim().toUpperCase();
            try {
                pageQuality = PixivDownload.PageQuality.valueOf(value);
            } catch(IllegalArgumentException e) {
                log.warn("群组ID [{}] - 无效的PageQuality设定值: {}", id, value);
            }
        }

        AutoSender sender = new RandomRankingArtworksSender(
                MessageSenderBuilder.getMessageSender(MessageSource.GROUP, id),
                id,
                rankingStart,
                rankingEnd,
                rankingMode, rankingContentType,
                pageQuality
        );

        RandomIntervalSendTimer timer = RandomIntervalSendTimer.createTimer(
                id,
                sender,
                setting.get("time.min").getAsLong(),
                setting.get("time.float").getAsInt(),
                true, true);
        log.info("群组 {} 已创建对应Timer: {}", id, Integer.toHexString(timer.hashCode()));
    }

    /**
     * 删除一个推送定时器
     * @param id 群号
     * @throws NoSuchElementException 当这个群号没有定时器的时候抛出异常
     */
    @Command
    public static String removePushGroup(@Argument(name = "$fromGroup") long fromGroup, @Argument(name = "group", force = false) long id) {
        long group = id <= 0 ? fromGroup : id;
        RandomIntervalSendTimer.getTimerById(group).destroy();
        pushInfoMap.remove(group);
        return "已关闭群 " + group + " 的美图推送功能。";
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

    @Command
    public static String getReportList() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Set<String> keys = BotCommandProcess.reportStore.keys();
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("当前被报告的作品列表：\n");
        int count = 1;
        for(String key : keys) {
            String illustIdStr = key.substring(key.indexOf(".") + 1);
            JsonObject report = BotCommandProcess.reportStore.getCache(illustIdStr).getAsJsonObject();
            log.debug("{} - Report: {}", illustIdStr, report);
            String reason = report.get("reason").isJsonNull() ? "" : report.get("reason").getAsString();
            msgBuilder.append(count).append(". 作品Id: ").append(illustIdStr)
                    .append("(").append(dateFormat.format(new Date(report.get("reportTime").getAsLong()))).append(")：\n")
                    .append("报告者QQ：").append(report.get("fromQQ").getAsLong()).append("\n")
                    .append("报告所在群：").append(report.get("fromGroup").getAsLong()).append("\n")
                    .append("报告原因：\n").append(reason).append("\n");
        }
        return msgBuilder.toString();
    }

    @Command
    public static String unBanArtwork(@Argument(name = "id") int illustId) {
        if(illustId <= 0) {
            return "无效的作品id!";
        }
        boolean removeResult = BotCommandProcess.reportStore.remove(String.valueOf(illustId));
        return removeResult ? "作品已解封！" : "解封失败！可能该作品并未被封禁。";
    }

}
