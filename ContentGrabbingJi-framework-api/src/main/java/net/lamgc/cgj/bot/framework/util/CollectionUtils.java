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

package net.lamgc.cgj.bot.framework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 额外的集合工具类.
 * @author LamGC
 */
public class CollectionUtils {

    /**
     * 通过 keySet 和取值接口构造对应 Map.
     * 处理时会自动跳过 key 为 null 的情况.
     * @param keySet 存储了所有 key 的 Set 对象.
     * @param valueSource 可根据 key 获取 value 的接口.
     * @param <K> 键类型.
     * @param <V> 值类型.
     * @return 返回对应 Map.
     */
    public static <K, V> Map<K, V> toMap(Set<K> keySet, Function<K, V> valueSource) {
        Map<K, V> map = new HashMap<>(keySet.size());
        keySet.forEach(key -> {
            if(key != null) {
                map.put(key, valueSource.apply(key));
            }
        });
        return map;
    }


}
