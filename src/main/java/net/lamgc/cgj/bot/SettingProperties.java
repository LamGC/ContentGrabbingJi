package net.lamgc.cgj.bot;

import com.google.common.base.Throwables;
import net.lamgc.cgj.bot.boot.BotGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class SettingProperties {

    private final static Logger log = LoggerFactory.getLogger(SettingProperties.class);

    private final static File globalPropFile = new File(getPropertiesDir(), "global.properties");
    private final static Properties globalProp = new Properties();

    private final static Map<Long, Properties> groupPropMap = new HashMap<>();

    private final static Set<Long> changeList = Collections.synchronizedSet(new HashSet<>());

    /**
     * 全局配置项
     */
    public final static long GLOBAL = 0;

    /**
     * 清空所有Properties.
     */
    public static void clearProperties() {
        groupPropMap.clear();
        globalProp.clear();
    }

    /**
     * 加载配置文件
     */
    public static void loadProperties() {
        loadGlobalProperties();

        File[] files = getPropertiesDir()
                .listFiles((dir, fileName) -> fileName.startsWith("group.") && fileName.endsWith(".properties"));
        if(files == null) {
            log.error("检索群组配置文件失败, 可能是被拒绝访问.");
            return;
        }

        for (File file : files) {
            String name = file.getName();
            long groupId;
            try {
                groupId = Long.parseLong(name.substring(name.indexOf("group.") + 6, name.lastIndexOf(".properties")));
            } catch (NumberFormatException e) {
                log.error("非法的配置文件名: {}", name);
                continue;
            }
            if(!groupPropMap.containsKey(groupId)) {
                groupPropMap.put(groupId, new Properties(globalProp));
            }

            loadGroupProperties(groupId, groupPropMap.get(groupId));
        }

    }

    /**
     * 保存配置项
     */
    public static void saveProperties() {
        log.info("正在保存所有配置...");
        saveGlobalProperties();

        for (Long groupId : groupPropMap.keySet()) {
            if(!changeList.contains(groupId)) {
                log.debug("群组 {} 配置无改动, 忽略保存.", groupId);
                return;
            }
            log.debug("正在保存群组 {} 配置文件...", groupId);
            saveGroupProperties(groupId);
        }
        log.info("配置保存完成.");
    }

    /**
     * 保存指定群组的配置文件
     * @param groupId 要保存配置的群组Id
     */
    private static void saveGroupProperties(long groupId) {
        try {
            saveGroupProperties(groupId, getGroupProperties(groupId));
        } catch (IOException e) {
            log.error("群组 {} 配置保存失败\n{}", groupId, Throwables.getStackTraceAsString(e));
        }
    }

    private static void saveGroupProperties(Long groupId, Properties properties) throws IOException {
        File groupPropFile = new File(getPropertiesDir(), "group." + groupId + ".properties");
        if(!groupPropFile.exists() && !groupPropFile.createNewFile()) {
            log.error("群组 {} 配置文件创建失败!", groupId);
            return;
        }

        saveProperties(properties, new FileOutputStream(groupPropFile));
    }

    private static void loadGlobalProperties() {
        if(globalPropFile.exists() && globalPropFile.isFile()) {
            log.info("正在加载全局配置文件...");
            try (Reader reader = new InputStreamReader(new FileInputStream(globalPropFile), StandardCharsets.UTF_8)) {
                globalProp.load(reader);
                log.info("全局配置文件加载完成.");
            } catch (IOException e) {
                log.error("加载全局配置文件时发生异常", e);
            }
        } else {
            log.info("未找到全局配置文件，跳过加载.");
        }
    }

    /**
     * 保存全局配置项
     */
    private static void saveGlobalProperties() {
        try {
            if(!globalPropFile.exists() && !globalPropFile.createNewFile()) {
                log.error("创建全局配置文件失败.");
                return;
            }

            saveProperties(globalProp, new FileOutputStream(globalPropFile));
        } catch (IOException e) {
            log.error("全局配置文件保存时发生异常", e);
        }
    }

    private static void loadGroupProperties(long groupId, Properties properties) {
        File propFile = new File(getPropertiesDir(), "group." + groupId + ".properties");
        Properties groupProp = Objects.requireNonNull(properties);
        if(!propFile.exists() || !propFile.isFile()) {
            log.warn("群组 {} 配置文件不存在, 或不是一个文件.({})", groupId, propFile.getAbsolutePath());
            return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(propFile), StandardCharsets.UTF_8)) {
            groupProp.load(reader);
        } catch (IOException e) {
            log.error("读取群组 {} 群配置文件时发生异常:\n{}", groupId, Throwables.getStackTraceAsString(e));
        }
    }

    private static void saveProperties(Properties properties, OutputStream stream) throws IOException {
        properties.store(new OutputStreamWriter(stream, StandardCharsets.UTF_8), null);
    }

    /**
     * 获取配置文件目录
     * @return 返回目录File对象.
     */
    private static File getPropertiesDir() {
        File propDir = new File(BotGlobal.getGlobal().getDataStoreDir(), "/setting/");
        if(!propDir.exists() && !propDir.mkdirs()) {
            log.warn("Setting文件夹创建失败!");
        }
        return propDir;
    }

    public static String getProperty(long groupId, String key) {
        return getProperty(groupId, key, null);
    }

    public static String getProperty(long groupId, String key, String defaultValue) {
        if(groupId <= 0) {
            return globalProp.getProperty(key, defaultValue);
        } else {
            Properties properties = groupPropMap.get(groupId);
            return properties == null ? defaultValue : properties.getProperty(key, defaultValue);
        }
    }

    /**
     * 设置配置项
     * @param groupId 群组Id, 如为0或负数则为全局配置
     * @param key 配置项key名
     * @param value 欲设置的新值, 如为null则删除该配置项
     * @return 返回上一次设定值
     */
    public static String setProperty(long groupId, String key, String value) {
        Objects.requireNonNull(key);
        Properties targetProperties;
        if(groupId <= 0) {
            targetProperties = globalProp;
        } else {
            changeList.add(groupId);
            targetProperties = getGroupProperties(groupId);
        }
        String lastValue = targetProperties.getProperty(key);
        if(value != null) {
            targetProperties.setProperty(key, value);
        } else {
            targetProperties.remove(key);
        }
        return lastValue;
    }

    /**
     * 获取GlobalProperties
     * @return 全局Properties
     */
    private static Properties getGlobalProperties() {
        return globalProp;
    }

    /**
     * 获取群组Properties
     * @param groupId 群组Id
     * @return 如果存在, 返回Properties, 不存在返回null.
     * @throws IllegalArgumentException 当群组Id 小于或等于0 时抛出.
     */
    private static Properties getGroupProperties(long groupId) {
        if (groupId <= 0) {
            throw new IllegalArgumentException("Group number cannot be 0 or negative: " + groupId);
        }
        if(!groupPropMap.containsKey(groupId)) {
            groupPropMap.put(groupId, new Properties(globalProp));
        }
        return groupPropMap.get(groupId);
    }

    /**
     * 获取群组 Properties, 如果指定群组没有 Properties, 则使用GlobalProperties.
     * @param groupId 指定群组Id
     * @return 如果群组存在所属Properties, 则返回群组Properties, 否则返回GlobalProperties.
     */
    public static Properties getProperties(long groupId) {
        if(groupPropMap.containsKey(groupId)) {
            return groupPropMap.get(groupId);
        }
        return getGlobalProperties();
    }

}
