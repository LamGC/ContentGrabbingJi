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

package net.lamgc.cgj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContentGrabbingJi 启动主类.
 *
 * 请不要在该类，甚至是该模块（指 exec 模块）开发任何与启动项目无关的功能！
 * @author LamGC
 */
public class ApplicationMain {

    private final static Logger log = LoggerFactory.getLogger(ApplicationMain.class);

    public static void main(String[] args) {
        log.info("ContentGrabbingJi 正在启动中...");
        beforeRun();
        log.info("初始化完成, 正在启动Core...");
        run();
        log.info("正在清理运行时内容...");
        afterRun();
        log.info("ContentGrabbingJi 退出.");
    }

    private static void beforeRun() {}

    private static void run() {}

    private static void afterRun() {}

}
