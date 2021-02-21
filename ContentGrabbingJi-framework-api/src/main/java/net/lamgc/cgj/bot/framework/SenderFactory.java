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

import net.lamgc.cgj.bot.framework.message.MessageSender;
import net.lamgc.cgj.bot.framework.message.MessageSource;
import org.pf4j.ExtensionPoint;

/**
 * 消息源发送器构造工厂.
 * <p> 该接口实现将由对应框架实现, 每个框架仅允许实现一个.
 * @author LamGC
 */
public interface SenderFactory extends ExtensionPoint {

    /**
     * 获取所属平台.
     * @return 返回平台信息对象.
     */
    Platform getPlatform();

    /**
     * 获取发送器.
     * @param source 消息源类型.
     * @param id 消息源 Id.
     * @return 返回消息发送器, 本方法不允许返回 null.
     * @throws NotFoundSenderException 当无法获取对应的消息源发送器时, 将抛出该异常.
     */
    MessageSender getSender(MessageSource source, long id) throws NotFoundSenderException;

}
