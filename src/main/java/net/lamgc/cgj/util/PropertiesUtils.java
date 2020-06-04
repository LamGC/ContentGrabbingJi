package net.lamgc.cgj.util;

import net.lamgc.utils.base.ArgumentsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertiesUtils {

    private final static Logger log = LoggerFactory.getLogger(PropertiesUtils.class);

    private PropertiesUtils() {}

    /**
     * 从ArgumentsProperties获取设置项到System Properties
     * @param prop ArgumentsProperties对象
     * @param key 设置项key
     * @param defaultValue 默认值
     * @return 如果成功从ArgumentsProperties获得设置项, 返回true, 如未找到(使用了defaultValue或null), 返回false;
     */
    public static boolean getSettingToSysProp(ArgumentsProperties prop, String key, String defaultValue) {
        if(prop.containsKey(key)) {
            log.info("{}: {}", key, prop.getValue(key));
            System.setProperty("cgj." + key, prop.getValue(key));
            return true;
        } else {
            if(defaultValue != null) {
                System.setProperty("cgj." + key, defaultValue);
            }
            return false;
        }
    }

    /**
     * 将环境变量的值读取并存入System Properties.
     * @param envKey 待获取的环境变量Key
     * @param sysPropKey 要设置的System Properties Key值
     * @param defaultValue 默认值, 可选
     * @return 如果设置成功, 返回true, 否则返回false
     */
    public static boolean getEnvSettingToSysProp(String envKey, String sysPropKey, String defaultValue) {
        String env = System.getenv(envKey);
        if(env != null) {
            System.setProperty("cgj." + sysPropKey, env);
            return true;
        } else if(defaultValue != null) {
            System.setProperty("cgj." + sysPropKey, defaultValue);
        }
        return false;
    }

}
