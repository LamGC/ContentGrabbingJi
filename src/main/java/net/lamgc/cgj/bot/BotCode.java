package net.lamgc.cgj.bot;

import com.google.common.base.Strings;

import java.util.*;
import java.util.regex.Pattern;

public class BotCode {

    public static BotCode build(String platformName, String functionName) {
        return build(platformName, functionName, null);
    }

    public static BotCode build(String platformName, String functionName, Map<String, String> parameter) {
        if(Strings.isNullOrEmpty(platformName)) {
            throw new IllegalArgumentException("platformName is Null or Empty.");
        } else if(Strings.isNullOrEmpty(functionName)) {
            throw new IllegalArgumentException("functionName is Null or Empty.");
        }

        return new BotCode(platformName, functionName, parameter);
    }

    private final static Pattern codePattern = Pattern.compile("\\[.*?:.*?]");
    public static BotCode parse(String str) {
        if (!codePattern.matcher(str).matches()) {
            throw new IllegalArgumentException("invalid string input: " + str);
        }

        String text = str.substring(1, str.length() - 1);
        String[] texts = text.split(",");
        if(texts.length <= 0) {
            throw new IllegalArgumentException("invalid string input: " + str);
        }

        String[] keys = texts[0].split(":", 2);
        if(keys.length != 2) {
            throw new IllegalArgumentException("invalid string input: " + str);
        }
        if(Strings.isNullOrEmpty(keys[0]) || Strings.isNullOrEmpty(keys[1])) {
            throw new IllegalArgumentException("invalid string input: " + str);
        }

        HashMap<String, String> param = new HashMap<>(texts.length - 1);
        for (int i = 1; i < texts.length; i++) {
            String[] items = texts[i].split("=");
            if(items.length != 2) {
                continue;
            }

            param.put(items[0].trim(), items[1]);
        }

        return new BotCode(keys[0], keys[1], param);
    }

    /**
     * 获取BotCode所使用的匹配正则表达式
     * @return 用于匹配BotCode的正则表达式对象
     */
    public static Pattern getCodePattern() {
        return Pattern.compile(codePattern.pattern());
    }

    private String platformName;
    private String functionName;
    private Hashtable<String, String> parameter = new Hashtable<>();

    /**
     * 构造一个机器功能码
     * @param platformName 平台代码
     * @param functionName 功能名
     * @param parameter 参数Map
     */
    private BotCode(String platformName, String functionName, Map<String, String> parameter) {
        this.platformName = platformName;
        this.functionName = functionName;
        if(parameter != null && !parameter.isEmpty()) {
            this.parameter.putAll(parameter);
        }
    }

    /**
     * 设置平台代码
     * @param platformName 欲设置的平台代码
     */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    /**
     * 获取平台代码
     * @return 当前设置的平台代码
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * 设置功能名
     * @param functionName 欲设置的新功能名
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * 获取功能名
     * @return 返回当前设置的功能名
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * 添加参数
     * @param key 参数键
     * @param value 参数值
     */
    public void addParameter(String key, String value) {
        parameter.put(key.trim(), value);
    }

    /**
     * 检查一个参数项是否存在
     * @param key 欲查询其存在的参数项所属键
     * @return 返回true则参数项存在
     */
    public boolean containsParameter(String key) {
        return parameter.containsKey(key.trim());
    }

    /**
     * 获取参数项的值
     * @param key 欲获取参数值的参数项所属键
     * @return 返回参数项的参数值
     */
    public String getParameter(String key) {
        return parameter.get(key.trim());
    }

    /**
     * 获取所有参数项的参数键
     * @return 返回存储了所有参数键的Set对象
     */
    public Set<String> parameterKeys() {
        return new HashSet<>(parameter.keySet());
    }

    /**
     * 将BotCode对象转为功能代码文本
     * 格式:
     * <pre>[Platform:Function, parameter...]</pre>
     * @return 功能代码文本
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[" + platformName + ":" + functionName);
        if(!parameter.isEmpty()) {
            builder.append(", ");
            parameter.forEach((key, value) -> builder.append(key).append("=").append(value).append(", "));
            builder.replace(builder.length() - 2, builder.length(), "");
        }
        return builder.append("]").toString();
    }
}
