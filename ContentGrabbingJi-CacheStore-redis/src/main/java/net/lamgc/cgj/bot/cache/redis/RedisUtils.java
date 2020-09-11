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

package net.lamgc.cgj.bot.cache.redis;

/**
 * @author LamGC
 */
public class RedisUtils {

    /**
     * 返回码 - 成功
     */
    public final static int RETURN_CODE_OK = 1;

    /**
     * 返回码 - 失败
     */
    public final static int RETURN_CODE_FAILED = 0;

    /**
     * Key匹配规则 - 所有Key
     */
    public final static String KEY_PATTERN_ALL = "*";

    /**
     * Key 分隔符
     */
    public final static String KEY_SEPARATOR = ":";

    /**
     * 检查字符串返回结果是否为操作成功.
     * @param result 字符串返回结果.
     * @return 如果为操作成功, 返回 true.
     */
    public static boolean isOk(String result) {
        return "OK".equalsIgnoreCase(result);
    }

}
