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

/**
 * 缓存容器类型.
 * @author LamGC
 */
public enum CacheStoreSource {
    /**
     * 内存存储(速度最快).
     */
    MEMORY,
    /**
     * 本地存储(单机存储).
     */
    LOCAL,
    /**
     * 远端存储(例如网络, 可多机读写且与单机无关).
     */
    REMOTE
}
