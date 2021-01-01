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

package net.lamgc.cgj.bot.framework;

import net.lamgc.cgj.bot.framework.message.BotCodeDescriptor;
import org.pf4j.PluginDescriptor;

import java.util.List;

/**
 * 框架描述对象.
 * @author LamGC
 */
public interface FrameworkDescriptor extends PluginDescriptor {

    /**
     * 获取框架所属平台.
     * @return 返回平台对象.
     */
    Platform getPlatform();

    /**
     * 获取 BotCode 描述.
     * @return 返回 BotCode 描述对象.
     */
    BotCodeDescriptor getBotCodeDescriptor();

    /**
     * 获取框架作者信息.
     * @return 返回存储了所有作者信息的 List.
     */
    List<Author> getAuthors();

}
