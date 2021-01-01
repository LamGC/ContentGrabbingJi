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

/**
 * 受 ContentGrabbingJi 完整支持的功能码功能名.
 * @see BotCode
 * @see net.lamgc.cgj.bot.framework.message.BotCodeFunction
 * @author LamGC
 */
public enum StandardBotCodeFunction implements BotCodeFunction {
    /**
     * 提醒某人.
     */
    AT("at", false),
    /**
     * 图片.
     */
    IMAGE("image", false),
    /**
     * Emoji 表情.
     */
    EMOJI("emoji", false),
    /**
     * 音频.
     */
    AUDIO("audio", true),
    /**
     * 文件.
     */
    FILE("file", true),
    ;

    private final String functionName;
    private final boolean headerFunction;

    StandardBotCodeFunction(String functionName, boolean headerFunction) {
        this.functionName = functionName;
        this.headerFunction = headerFunction;
    }

    @Override
    public String getFunctionName() {
        return this.functionName;
    }

    @Override
    public boolean headerFunction() {
        return headerFunction;
    }

}
