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

package net.lamgc.cgj.bot.cache;

import java.util.Map;
import java.util.Set;

/**
 * Map 缓存存储容器.
 * @author LamGC
 */
public interface MapCacheStore<V> extends CacheStore<Map<String, V>> {

    /**
     * 获取 Map 字段数量.
     * @param key Map 缓存项的键名.
     * @return 返回 Map 字段数量, 如果缓存项不存在或获取失败, 返回 -1.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    int mapSize(String key);

    /**
     * 获取 Map 字段集合.
     * @param key 待查询的 Map 缓存项的键名.
     * @return 返回 Map 字段集合, 如果缓存项不存在, 返回 null.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    Set<String> mapFieldSet(String key);

    /**
     * 获取 Map 字段值集合.
     * @param key 待查询的 Map 缓存项的键名.
     * @return 返回 Map 字段值集合, 如果缓存项不存在, 返回 null.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    Set<V> mapValueSet(String key);

    /**
     * 将指定的值与此映射中的指定字段关联.
     * @param key Map 缓存项的键名.
     * @param field 字段名.
     * @param value 字段值.
     * @return 如果成功返回 true.
     * @throws NullPointerException 当 key/field/value 为 null 时抛出, 缓存存储容器不允许出现 null 值.
     */
    boolean put(String key, String field, V value);

    /**
     * 添加一组字段.
     * @param key Map 缓存项的键名.
     * @param map 待添加的 Map.
     * @return 如果成功返回 true.
     * @throws NullPointerException 当 key/map 为 null 时抛出, 缓存存储容器不允许出现 null 值.
     */
    boolean putAll(String key, Map<String, V> map);

    /**
     * 如果字段不存在, 则会将指定的值与此映射中的指定字段关联.
     *
     * <p>该方法与 {@link #put(String, String, Object)} 类似, 但如果字段存在, 将不会执行任何操作并以失败返回.
     * @param key Map 缓存项的键名.
     * @param field 字段名.
     * @param value 字段值.
     * @return 如果字段不存在且设置成功, 返回 true, 否则返回 false.
     * @throws NullPointerException 当 key/field/value 为 null 时抛出, 缓存存储容器不允许出现 null 值.
     */
    boolean putIfNotExist(String key, String field, V value);

    /**
     * 获取指定字段的字段值.
     * @param key Map 缓存项的键名.
     * @param field 字段名.
     * @return 如果 Map 缓存项存在且字段存在, 返回字段的对应值.
     * @throws NullPointerException 当 key/field 为 null 时抛出.
     */
    V get(String key, String field);

    /**
     * 删除 Map 中的指定字段.
     * @param key Map 缓存项的键名.
     * @param field 待删除的字段名.
     * @return 如果 Map 缓存项存在, 字段存在并且删除成功, 返回 true.
     * @throws NullPointerException 当 key/field 为 null 时抛出.
     */
    boolean removeField(String key, String field);

    /**
     * 检查 Map 中是否有指定字段.
     * @param key Map 缓存项的键名.
     * @param field 待检查的字段名.
     * @return 如果 Map 缓存项存在且字段存在, 返回 true.
     * @throws NullPointerException 当 key/field 为 null 时抛出.
     */
    boolean containsField(String key, String field);

    /**
     * 检查 Map 是否为空(没有任何字段).
     *
     * <p>该方法等价于 {@code mapSize(key) == 0}
     * @param key Map 缓存项的键名.
     * @return 如果 Map 缓存项存在且为空, 返回 true.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    boolean mapIsEmpty(String key);

    /**
     * 清空 Map 中的所有字段(并不会删除 Map 缓存项).
     * @param key 待清空的 Map 缓存项键名.
     * @return 如果存在且清空成功, 返回 true.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    boolean clearMap(String key);

}
