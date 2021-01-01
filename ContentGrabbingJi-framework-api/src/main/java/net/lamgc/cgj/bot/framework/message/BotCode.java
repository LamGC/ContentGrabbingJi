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

import net.lamgc.cgj.bot.framework.Platform;

import java.util.Set;

/**
 * 功能码接口.
 * @author LamGC
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface BotCode extends Message {

    /**
     * 获取 BotCode 实现所属平台.
     * @return 返回所属平台.
     */
    Platform getPlatform();

    /**
     * 取功能函数名.
     * @return 返回功能函数名.
     */
    BotCodeFunction getFunction();

    /**
     * 设置功能函数名
     * @param function 新的功能函数名.
     */
    void setFunction(BotCodeFunction function);

    /**
     * 设置功能参数
     * @param key 参数名
     * @param value 参数值, 如果参数值为 {@code null} 则删除该参数.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    void setProperty(String key, String value);

    /**
     * 获取功能参数
     * @param key 参数名
     * @return 如果存在, 返回参数名, 否则返回 {@code null}.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    String getProperty(String key);

    /**
     * 取功能码参数键集.
     * @return 返回存储了所有参数键名的Set.
     */
    Set<String> getPropertiesKeys();

    /**
     * 检查指定参数是否存在.
     * @param key 待检查的参数名.
     * @return 如果存在, 返回 {@code true}.
     * @throws NullPointerException 当 key 为 {@code null} 时抛出.
     */
    boolean hasProperty(String key);

}
