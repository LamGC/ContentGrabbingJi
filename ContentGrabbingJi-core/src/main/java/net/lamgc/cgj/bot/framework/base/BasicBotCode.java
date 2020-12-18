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

package net.lamgc.cgj.bot.framework.base;

import net.lamgc.cgj.bot.framework.Platform;
import net.lamgc.cgj.bot.framework.message.AbstractBotCode;
import net.lamgc.cgj.bot.framework.message.BotCode;
import net.lamgc.cgj.bot.framework.message.BotCodeFunction;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * ContentGrabbingJi 内部 BotCode 实现.
 * <p> 参数形式与 Url 查询参数格式一致.
 * @author LamGC
 */
public class BasicBotCode extends AbstractBotCode {

    private final static Platform PLATFORM = new Platform("ContentGrabbingJi", "CGJ");

    public BasicBotCode(BotCodeFunction function) {
        super(function);
    }

    public BasicBotCode(BotCode botCode) {
        super(botCode);
    }

    public BasicBotCode(BotCodeFunction function, Map<String, String> functionProperties) {
        super(function, functionProperties);
    }

    @Override
    public Platform getPlatform() {
        return PLATFORM;
    }

    @Override
    public String contentToString() {
        StringBuilder builder = new StringBuilder('[' + getFunction().getFunctionName());
        if (getPropertiesKeys().size() == 0) {
            return builder.append(']').toString();
        } else {
            builder.append(':');
            for (String key : getPropertiesKeys()) {
                try {
                    builder.append(key).append('=')
                            .append(URLEncoder.encode(getProperty(key), "UTF-8")).append('&');
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException(e);
                }
            }
            return builder.deleteCharAt(builder.lastIndexOf("&")).append(']').toString();
        }
    }

}
