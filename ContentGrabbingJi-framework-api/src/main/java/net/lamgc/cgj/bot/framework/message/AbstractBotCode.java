/*
 * Copyright (C) 2021  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * ContentGrabbingJi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lamgc.cgj.bot.framework.message;

import com.google.common.base.Strings;
import net.lamgc.cgj.bot.framework.util.CollectionUtils;

import java.util.*;

/**
 * 可用于快速实现的抽象功能码.
 * @author LamGC
 * @see BotCode
 */
public abstract class AbstractBotCode implements BotCode {

    private BotCodeFunction function;
    private final Map<String, String> functionProperties = new Hashtable<>();

    public AbstractBotCode(BotCodeFunction function) {
        this(function, null);
    }

    /**
     * 将其他实现的 BotCode 转换成该实现的 BotCode.
     * @param botCode 待转换的 BotCode.
     */
    public AbstractBotCode(BotCode botCode) {
        this(botCode.getFunction(), CollectionUtils.toMap(botCode.getPropertiesKeys(), botCode::getProperty));
    }

    /**
     * 根据给定的功能名和参数 Map 构造 BotCode.
     * @param function 功能名
     * @param functionProperties 参数集 Map. 如果不需要可传入 null.
     */
    public AbstractBotCode(BotCodeFunction function, Map<String, String> functionProperties) {
        if (Strings.isNullOrEmpty(function.getFunctionName())) {
            throw new IllegalArgumentException("functionName is null or empty");
        }
        this.function = function;
        if(functionProperties != null && !functionProperties.isEmpty()) {
            this.functionProperties.putAll(functionProperties);
        }
    }

    @Override
    public String toString() {
        StringBuilder mapString = new StringBuilder(functionProperties.getClass().getSimpleName());
        if (!functionProperties.isEmpty()) {
            mapString.append("{");
            functionProperties.forEach((key, value) -> {
                // "key"="value"
                mapString.append('"').append(key).append("\"='").append(value).append("', ");

            });
            mapString.delete(mapString.length() - 2, mapString.length());
        }
        return this.getClass().getSimpleName() + '@' + Integer.toHexString(this.hashCode()) + '{' +
                "Platform=" + getPlatform() + ", " +
                "functionName='" + function.getFunctionName() + '\'' +
                ", functionProperties={" + mapString.toString() + '}' +
                '}';
    }

    /**
     * 取功能函数名.
     * @return 返回功能函数名.
     */
    @Override
    public BotCodeFunction getFunction() {
        return function;
    }

    /**
     * 设置功能函数名
     * @param function 新的功能函数名.
     */
    @Override
    public void setFunction(BotCodeFunction function) {
        this.function = function;
    }

    /**
     * 设置功能参数
     * @param key 参数名
     * @param value 参数值, 如果参数值为 {@code null} 则删除该参数.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    @Override
    public void setProperty(String key, String value) {
        Objects.requireNonNull(key);
        if(value == null) {
            functionProperties.remove(key);
            return;
        }
        functionProperties.put(key, value);
    }

    /**
     * 获取功能参数
     * @param key 参数名
     * @return 如果存在, 返回参数名, 否则返回 {@code null}.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    @Override
    public String getProperty(String key) {
        return functionProperties.get(Objects.requireNonNull(key));
    }

    /**
     * 获取参数 Keys 集合.
     * <p> 注意: 通过本方法返回的 Set 不可进行修改.
     * @return 返回包含所有参数 Keys 的 Set 对象.
     */
    @Override
    public Set<String> getPropertiesKeys() {
        return Collections.unmodifiableSet(functionProperties.keySet());
    }

    @Override
    public boolean hasProperty(String key) {
        return functionProperties.containsKey(Objects.requireNonNull(key));
    }
}
