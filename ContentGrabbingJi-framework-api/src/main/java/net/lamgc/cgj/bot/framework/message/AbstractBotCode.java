/*
 * Copyright (C) 2020  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
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

import net.lamgc.cgj.bot.framework.util.CollectionUtils;

import java.util.*;

/**
 * 可用于快速实现的抽象功能码.
 * @author LamGC
 * @see BotCode
 */
public abstract class AbstractBotCode implements BotCode {

    private String functionName;
    private final Map<String, String> functionProperties = new Hashtable<>();

    public AbstractBotCode() {}

    /**
     * 将其他实现的 BotCode 转换成该实现的 BotCode.
     * @param botCode 待转换的 BotCode.
     */
    public AbstractBotCode(BotCode botCode) {
        this(botCode.getFunctionName(), CollectionUtils.toMap(botCode.getPropertiesKeys(), botCode::getProperty));
    }

    /**
     * 根据给定的功能名和参数 Map 构造 BotCode.
     * @param functionName 功能名
     * @param functionProperties 参数集 Map. 如果不需要可传入 null.
     */
    public AbstractBotCode(String functionName, Map<String, String> functionProperties) {
        this.functionName = functionName;
        if(functionProperties != null) {
            this.functionProperties.putAll(functionProperties);
        }
    }

    @Override
    public String toString() {
        StringBuilder mapString = new StringBuilder(functionProperties.getClass().getSimpleName() + "{");
        functionProperties.forEach((key, value) -> mapString.append(key).append("='").append(value).append("', "));
        return "AbstractBotCode{" +
                "Platform=" + getPlatform() + ", " +
                "functionName='" + functionName + '\'' +
                ", functionProperties=" + mapString.substring(0, mapString.length() - 2) +
                '}';
    }

    /**
     * 取功能函数名.
     * @return 返回功能函数名.
     */
    @Override
    public String getFunctionName() {
        return functionName;
    }

    /**
     * 设置功能函数名
     * @param functionName 新的功能函数名.
     */
    @Override
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
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

    @Override
    public Set<String> getPropertiesKeys() {
        return Collections.unmodifiableSet(functionProperties.keySet());
    }

}
