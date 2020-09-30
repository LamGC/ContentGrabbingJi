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

package net.lamgc.cgj.bot.cache.local;

import net.lamgc.cgj.bot.cache.*;
import net.lamgc.cgj.bot.cache.convert.StringConverter;

/**
 * 本地缓存存储容器工厂.
 * 最快速但又是最占内存的方法, 适用于远端缓存失效, 或无远端缓存的情况下使用.
 * 最简单的缓存实现, 无持久化功能.
 * @author LamGC
 */
@Factory(name = "Local", priority = FactoryPriority.PRIORITY_LOWEST, source = CacheStoreSource.MEMORY)
public class LocalCacheStoreFactory implements CacheStoreFactory {

    @Override
    public <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) {
        return new HashSingleCacheStore<>();
    }

    @Override
    public <V> ListCacheStore<V> newListCacheStore(String identify, StringConverter<V> converter) {
        return new CopyOnWriteArrayListCacheStore<>();
    }

    @Override
    public <V> SetCacheStore<V> newSetCacheStore(String identify, StringConverter<V> converter) {
        return new HashSetCacheStore<>();
    }

    @Override
    public <V> MapCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter) {
        return new HashMapCacheStore<>();
    }

    /**
     * 内存使用阀值.
     * <p>当内存使用到了指定百分比时, 将禁止创建 CacheStore.
     */
    private final static double MEMORY_USAGE_THRESHOLD = 85;
    @Override
    public boolean canGetCacheStore() {
        Runtime runtime = Runtime.getRuntime();
        double memoryUsedPercentage = (double) runtime.totalMemory() / runtime.maxMemory();
        return memoryUsedPercentage < MEMORY_USAGE_THRESHOLD;
    }
}
