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

package net.lamgc.cgj.bot.framework;

import java.util.Objects;

/**
 * 框架所属平台.
 * <p> 用于标识一个平台, 每个平台有唯一的 {@link #platformIdentify PlatformIdentify}.
 * <p> {@link #platformName PlatformName} 是允许不同的,
 * 只要 {@link #platformIdentify PlatformIdentify} 唯一即可.
 *
 * <p> {@link #platformIdentify PlatformIdentify}
 * 遵循 Camel-Case (骆峰命名法, 每个单词之间无空格, 单词首字母大写),
 * 对于平台标识由少量字母组成的则全大写(例如腾讯QQ的平台标识则为 'QQ' 而不是 'qq' 或 'Qq').
 * @author LamGC
 */
public final class Platform {

    private final String platformName;
    private final String platformIdentify;

    /**
     * 构造一个 Platform 对象.
     * @param platformName 平台名
     * @param platformIdentify 平台唯一标识名.
     */
    public Platform(String platformName, String platformIdentify) {
        this.platformName = Objects.requireNonNull(platformName, "PlatformName is null");
        this.platformIdentify = Objects.requireNonNull(platformIdentify, "PlatformIdentify is null");
    }

    @Override
    public String toString() {
        return "Platform{" +
                "platformName='" + platformName + '\'' +
                ", platformIdentify='" + platformIdentify + '\'' +
                '}';
    }

    /**
     * 获取平台名称.
     * @return 返回平台名称.
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * 获取平台唯一标识.
     * 注意, 该标识将应用于平台所属事件的处理相关.
     * @return 返回平台标识.
     */
    public String getPlatformIdentify() {
        return platformIdentify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Platform platform = (Platform) o;
        return platformIdentify.equals(platform.platformIdentify);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platformIdentify);
    }
}
