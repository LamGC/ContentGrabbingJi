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

package net.lamgc.cgj.bot.cache.redis;

import net.lamgc.cgj.bot.cache.CacheKey;

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
     * Key 匹配规则 - 所有 Key
     */
    public final static String KEY_PATTERN_ALL = "*";

    /**
     * 特殊缓存键 - 所有 Key.
     */
    public final static CacheKey CACHE_KEY_ALL = new CacheKey(KEY_PATTERN_ALL);

    /**
     * Key 分隔符
     */
    public final static String KEY_SEPARATOR = ":";

    /**
     * Redis 组件配置文件名.
     */
    public final static String PROPERTIES_FILE_NAME = "redis.properties.json";

    /**
     * 检查字符串返回结果是否为操作成功.
     * @param result 字符串返回结果.
     * @return 如果为操作成功, 返回 true.
     */
    public static boolean isOk(String result) {
        return "OK".equalsIgnoreCase(result);
    }

    /**
     * 将 {@link CacheKey} 转换为 Redis 的标准 key 格式.
     * @param keyPrefix Key 前缀.
     * @param cacheKey 缓存键对象.
     * @return 返回格式化后的 Key.
     */
    public static String toRedisCacheKey(String keyPrefix, CacheKey cacheKey) {
        return (keyPrefix.endsWith(KEY_SEPARATOR) ? keyPrefix : (keyPrefix + KEY_SEPARATOR)) +
                cacheKey.join(RedisUtils.KEY_SEPARATOR);
    }

}
