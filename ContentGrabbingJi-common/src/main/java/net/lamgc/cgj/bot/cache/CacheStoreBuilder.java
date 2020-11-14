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

import com.google.common.base.Throwables;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.exception.GetCacheStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * CacheStore 构造器.
 *
 * <p>这只是个门面类, 最终调用 {@link CacheStoreFactory} 的具体实现类获取 {@link CacheStore} 对象.
 * <p> CacheStoreBuilder 确保了返回不为 null,
 * 即使所有 Factory 无法返回合适的 CacheStore 实例, 也只会抛出 {@link NoSuchFactoryException} 异常.
 * @see CacheStoreFactory
 * @author LamGC
 */
public final class CacheStoreBuilder {

    private final static Logger log = LoggerFactory.getLogger(CacheStoreBuilder.class);

    private volatile List<CacheStoreFactory> factoryList;
    private final Map<CacheStoreFactory, FactoryInfo> factoryInfoMap = new Hashtable<>();
    private final ServiceLoader<CacheStoreFactory> factoryLoader = ServiceLoader.load(CacheStoreFactory.class);
    private final File dataDirectory;

    /**
     * 获取 CacheStoreBuilder 实例.
     * <p> 该方法仅提供给 ContentGrabbingJiBot 调用, 请勿通过该方法私自创建 CacheStoreBuilder.
     * @param cacheDataDirectory 缓存组件数据目录的根目录(例如提供 File 为 {@code /data/cache},
     *                           缓存组件名为 {@code example}, 则缓存组件的缓存路径为 {@code /data/cache/example}).
     * @return 返回新的 CacheStoreBuilder 实例, 各实例之间是没有任何关系的(包括创建的 CacheStore, 除非缓存组件设计错误).
     * @throws IOException 当路径检查失败时抛出.
     */
    public static CacheStoreBuilder getInstance(File cacheDataDirectory) throws IOException {
        Objects.requireNonNull(cacheDataDirectory, "Cache component data directory is null");
        if (!cacheDataDirectory.exists() && !cacheDataDirectory.mkdirs()) {
            throw new IOException("Data directory creation failed: " + cacheDataDirectory.getAbsolutePath());
        } else if (!cacheDataDirectory.isDirectory()) {
            throw new IOException("The specified data store path is not a directory: " +
                    cacheDataDirectory.getAbsolutePath());
        } else if (!cacheDataDirectory.canRead() || !cacheDataDirectory.canWrite()) {
            throw new IOException("The specified data store directory cannot be read or written.");
        }
        return new CacheStoreBuilder(cacheDataDirectory);
    }

    private CacheStoreBuilder(File dataDirectory) {
        this.dataDirectory = dataDirectory;
        loadFactory();
    }

    /**
     * 使用 SPI 机制加载所有缓存组件.
     *
     * <p>第一次执行时加载, 由 {@link #getFactory(CacheStoreSource, Function)} 调用.
     * <p>由于 ServiceLoader 线程不安全, 所以通过 synchronized 保证其安全性.
     * 不通过 块进行初始化的原因是因为担心发生异常导致无法继续执行
     * (除非必要, 否则不要使用 执行可能会发生异常的代码.).
     */
    private synchronized void loadFactory() {
        factoryLoader.reload();
        List<CacheStoreFactory> newFactoryList = new ArrayList<>();
        try {
            for (CacheStoreFactory factory : factoryLoader) {
                FactoryInfo info;
                try {
                    info = new FactoryInfo(factory.getClass());
                    if (factoryInfoMap.containsValue(info)) {
                        log.warn("发现 Name 重复的 Factory, 已跳过. (被拒绝的实现: {})", factory.getClass().getName());
                        continue;
                    }
                    factoryInfoMap.put(factory, info);
                } catch (IllegalArgumentException e) {
                    log.warn("Factory {} 加载失败: {}", factory.getClass().getName(), e.getMessage());
                    continue;
                }


                if (!initialFactory(factory, info)) {
                    log.warn("Factory {} 初始化失败.", info.getFactoryName());
                    continue;
                }
                newFactoryList.add(factory);
                log.info("Factory {} 已加载(优先级: {}, 实现类: {}).",
                        info.getFactoryName(),
                        info.getFactoryPriority(),
                        factory.getClass().getName());
            }
            newFactoryList.sort(new PriorityComparator());
            factoryList = newFactoryList;
            optimizeFactoryInfoMap();
        } catch (Error error) {
            // 防止发生 Error 又不输出到日志导致玄学问题难以排查.
            log.error("加载 CacheStoreFactory 时发生严重错误.", error);
            throw error;
        }
    }

    /**
     * 清除无效的 {@link FactoryInfo}
     */
    private void optimizeFactoryInfoMap() {
        factoryInfoMap.keySet().removeIf(factory -> !factoryList.contains(factory));
    }

    /**
     * 初始化 Factory.
     * @param factory Factory 对象.
     * @param info Factory Info.
     * @return 初始化成功则返回 {@code true}, 否则返回 {@code false}.
     */
    private boolean initialFactory(CacheStoreFactory factory, FactoryInfo info) {
        File factoryDataDirectory = new File(dataDirectory, info.getFactoryName());
        if (!factoryDataDirectory.exists() && !factoryDataDirectory.mkdirs()) {
            log.warn("Factory {} 数据存储目录创建失败, 可能会影响后续操作. (Path: {})",
                    info.getFactoryName(),
                    factoryDataDirectory.getAbsolutePath());
        }
        try {
            factory.initial(factoryDataDirectory);
            return true;
        } catch (Exception e) {
            log.error("Factory {} 初始化失败.\n{}", info.getFactoryName(), Throwables.getStackTraceAsString(e));
            return false;
        }
    }
    
    /**
     * 优先级排序器.
     */
    private final class PriorityComparator implements Comparator<CacheStoreFactory> {
        @Override
        public int compare(CacheStoreFactory o1, CacheStoreFactory o2) {
            FactoryInfo info1 = Objects.requireNonNull(factoryInfoMap.get(o1));
            FactoryInfo info2 = Objects.requireNonNull(factoryInfoMap.get(o2));
            return info2.getFactoryPriority() - info1.getFactoryPriority();
        }
    }

    /**
     * 获取一个当前可用的高优先级 Factory 对象.
     * @return 返回可用的高优先级 Factory 对象.
     */
    private <R extends CacheStore<?>> R getFactory(CacheStoreSource storeSource,
                                                   Function<CacheStoreFactory, R> function)
    throws NoSuchFactoryException {
        Iterator<CacheStoreFactory> iterator = factoryList.iterator();
        while (iterator.hasNext()) {
            CacheStoreFactory factory = iterator.next();
            FactoryInfo info = factoryInfoMap.get(factory);
            if (storeSource != null && info.getStoreSource() != storeSource) {
                continue;
            }
            try {
                if (factory.canGetCacheStore()) {
                    log.debug("CacheStoreFactory {} 可用(优先级: {}).", info.getFactoryName(), info.getFactoryPriority());
                } else {
                    continue;
                }
            } catch (Exception e) {
                log.error("CacheStoreFactory " + info.getFactoryName() +
                        " (" + factory.getClass().getName() + ") 检查可用性时发生异常", e);
                continue;
            }

            try {
                R result = function.apply(factory);
                log.trace("已通过 Factory '{}' 获取 CacheStore '{}'. (Factory实现类: {}).",
                        info.getFactoryName(),
                        result.getClass().getName(),
                        factory.getClass().getName());
                return result;
            } catch (Exception e) {
                if (!iterator.hasNext()) {
                    throw new NoSuchFactoryException(new GetCacheStoreException("CacheStoreFactory " +
                            info.getFactoryName() + " (" + factory.getClass().getName() +
                            ") 创建 CacheStore 时发生异常.", e));
                } else {
                    if (e instanceof GetCacheStoreException) {
                        log.warn("CacheStoreFactory '{} ({})' 无法提供相应 CacheStore. 原因: {}",
                                info.getFactoryName(), factory.getClass().getName(), e.getMessage());
                    } else {
                        log.warn("CacheStoreFactory '" + info.getFactoryName() +
                                " (" + factory.getClass().getName() + ")' 创建 CacheStore 时发生异常.", e);
                    }
                }
            }
        }
        throw new NoSuchFactoryException();
    }

    /**
     * 检查是否为 {@code null}.
     * @param cacheStore 缓存库.
     * @param factory 工厂对象.
     * @param <V> 缓存库类型.
     * @return 如果不为 null, 则正常返回.
     * @throws GetCacheStoreException 当 cacheStore 为 {@code null} 时抛出.
     */
    private <V extends CacheStore<?>> V returnRequireNonNull(V cacheStore, CacheStoreFactory factory) {
        if (cacheStore == null) {
            throw new GetCacheStoreException("Factory '" + factory.getClass().getName() + "' returned null");
        }
        return cacheStore;
    }

    /**
     * 获取单项缓存存储容器.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <V> 值类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) {
        return newSingleCacheStore(null, identify, converter);
    }

    /**
     * 获取单项缓存存储容器.
     * @param storeSource 存储类型.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <V> 值类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public <V> SingleCacheStore<V> newSingleCacheStore(CacheStoreSource storeSource, String identify,
                                                              StringConverter<V> converter) {
        try {
            return getFactory(storeSource, factory ->
                    returnRequireNonNull(factory.newSingleCacheStore(identify, converter), factory));
        } catch (NoSuchFactoryException e) {
            throw new GetCacheStoreException("无可用的 Factory.", e);
        }
    }

    /**
     * 获取列表缓存存储容器.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <E> 元素类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public <E> ListCacheStore<E> newListCacheStore(String identify, StringConverter<E> converter) {
        return newListCacheStore(null, identify, converter);
    }

    /**
     * 获取列表缓存存储容器.
     * @param storeSource 存储类型.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <E> 元素类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public <E> ListCacheStore<E> newListCacheStore(CacheStoreSource storeSource, String identify,
                                                          StringConverter<E> converter) {
        try {
            return getFactory(storeSource, factory ->
                    returnRequireNonNull(factory.newListCacheStore(identify, converter), factory));
        } catch (NoSuchFactoryException e) {
            throw new GetCacheStoreException("无可用的 Factory.", e);
        }
    }

    /**
     * 获取集合缓存存储容器.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <E> 元素类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public <E> SetCacheStore<E> newSetCacheStore(String identify, StringConverter<E> converter) {
        return newSetCacheStore(null, identify, converter);
    }

    /**
     * 获取集合缓存存储容器.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <E> 元素类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public <E> SetCacheStore<E> newSetCacheStore(CacheStoreSource storeSource, String identify,
                                                        StringConverter<E> converter) {
        try {
            return getFactory(storeSource, factory ->
                    returnRequireNonNull(factory.newSetCacheStore(identify, converter), factory));
        } catch (NoSuchFactoryException e) {
            throw new GetCacheStoreException("无可用的 Factory.", e);
        }
    }

    /**
     * 获取映射表缓存存储容器.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <V> 值类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public <V> MapCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter) {
        return newMapCacheStore(null, identify, converter);
    }

    /**
     * 获取映射表缓存存储容器.
     * @param storeSource 存储类型.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <V> 值类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public <V> MapCacheStore<V> newMapCacheStore(CacheStoreSource storeSource, String identify,
                                                        StringConverter<V> converter) {
        try {
            return getFactory(storeSource, factory ->
                    returnRequireNonNull(factory.newMapCacheStore(identify, converter), factory));
        } catch (NoSuchFactoryException e) {
            throw new GetCacheStoreException("无可用的 Factory.", e);
        }
    }

}
