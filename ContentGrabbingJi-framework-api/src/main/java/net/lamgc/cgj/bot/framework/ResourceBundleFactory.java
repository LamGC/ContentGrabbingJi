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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * ResourceBundle 构造工厂接口.
 * 如实现该接口, 可允许让 Core 使用框架自带的 ResourceBundle, 而不使用全局范围的 ResourceBundle.
 * @author LamGC
 */
public interface ResourceBundleFactory {

    /**
     * 获取指定 Locale 的 ResourceBundle 对象.
     * @param locale 地区对象.
     * @return 返回 ResourceBundle 对象.
     * @throws MissingResourceException 如果无法找到地区对应的 ResourceBundle 的同时,
     *                                  也无法获取 Default ResourceBundle 的话可抛出该异常.
     *                                  当 Core 捕获到该异常时, 将会使用全局范围的 ResourceBundle.
     */
    ResourceBundle getBundle(Locale locale) throws MissingResourceException;

}
