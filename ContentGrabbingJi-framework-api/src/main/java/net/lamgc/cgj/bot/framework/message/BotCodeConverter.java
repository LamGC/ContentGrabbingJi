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

import net.lamgc.cgj.bot.framework.message.exception.BuildBotCodeException;
import net.lamgc.cgj.bot.framework.message.exception.InvalidBotCodeException;
import net.lamgc.cgj.bot.framework.message.exception.UnsupportedBotCodeException;

/**
 * BotCode 转换器.
 * @author LamGC
 */
public interface BotCodeConverter {

    /**
     * 转换为平台或框架可识别并处理的 BotCode 字符串形式.
     * @param botCode BotCode 对象.
     * @return 返回转换后的结果.
     * @throws UnsupportedBotCodeException 当框架不支持该 BotCode 时抛出.
     * @throws BuildBotCodeException 当 BotCode 无法构造出字符串形式时抛出, 包含原因.
     */
    String toBotCodeString(BotCode botCode) throws UnsupportedBotCodeException, BuildBotCodeException;

    /**
     * 从 BotCode 字符串转换成 BotCode 对象.
     * @param botCodeString 传入的 BotCode 字符串.
     * @throws InvalidBotCodeException 当传入的 BotCode 字符串无法转换成该实现对应的 BotCode 对象时可抛出该异常,
     *                                 务必在异常中清晰说明异常原因.
     * @throws UnsupportedBotCodeException 当框架不支持该 BotCode 时抛出.
     */
    void fromBotCodeString(String botCodeString) throws InvalidBotCodeException, UnsupportedBotCodeException;

}
