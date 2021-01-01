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

package net.lamgc.cgj.bot.cache;

import java.util.Arrays;
import java.util.Objects;

/**
 * 缓存键.
 * 可根据不同缓存实现的需要调整名称.
 * @author LamGC
 */
public final class CacheKey {

    /**
     * 默认分隔符.
     */
    public final static String DEFAULT_SEPARATOR = ".";

    private final String[] keys;

    /**
     * 创建一个缓存键名.
     * @param first 组成键名的第一个部分.
     * @param keyStrings 键名的其余组成部分.
     * @throws NullPointerException 当 keyStrings 为 null 时抛出.
     */
    public CacheKey(String first, String... keyStrings) {
        Objects.requireNonNull(first);
        if (keyStrings == null || keyStrings.length == 0) {
            this.keys = new String[] {first};
        } else {
            checkKeyStrings(keyStrings);
            this.keys = new String[keyStrings.length + 1];
            this.keys[0] = first;
            System.arraycopy(keyStrings, 0, this.keys, 1, keyStrings.length);
        }
    }

    /**
     * 提供一组字符串用于组成缓存键.
     * @param keyStrings 键名组成数组.
     */
    public CacheKey(String[] keyStrings) {
        Objects.requireNonNull(keyStrings);
        checkKeyStrings(keyStrings);
        if (keyStrings.length == 0) {
            throw new IllegalArgumentException("Provide at least one element that makes up the key");
        }
        this.keys = keyStrings;
    }

    private void checkKeyStrings(String[] keyStrings) {
        for (String keyString : keyStrings) {
            if (keyString == null) {
                throw new NullPointerException("KeyStrings contains null");
            }
        }
    }

    /**
     * 获取组成 Key 的字符串数组.
     * @return 返回用于组成 Key 的字符串数组.
     */
    public String[] getKeyArray() {
        return keys;
    }

    /**
     * 使用指定分隔符组成完整 Key.
     * @param separator 分隔符.
     * @return 返回组装后的完整 Key.
     */
    public String join(String separator) {
        return String.join(separator, keys);
    }

    @Override
    public String toString() {
        return join(DEFAULT_SEPARATOR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheKey cacheKey = (CacheKey) o;
        return Arrays.equals(keys, cacheKey.keys);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keys);
    }
}
