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

/**
 * 框架平台DO.
 * @author LamGC
 */
public class Platform {

    private final String platformName;
    private final String platformIdentify;

    public Platform(String platformName, String platformIdentify) {
        this.platformName = platformName;
        this.platformIdentify = platformIdentify;
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

}
