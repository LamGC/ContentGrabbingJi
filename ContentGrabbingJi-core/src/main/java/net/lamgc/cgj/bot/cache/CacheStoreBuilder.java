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

import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.exception.GetCacheStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class CacheStoreBuilder {

    private final static Logger log = LoggerFactory.getLogger(CacheStoreBuilder.class);
    private final static List<CacheStoreFactory> FACTORY_LIST = new LinkedList<>();
    private final static Map<CacheStoreFactory, FactoryInfo> FACTORY_INFO_MAP = new Hashtable<>();

    /**
     * 使用 SPI 机制加载所有缓存组件.
     *
     * <p>第一次执行时加载, 由 {@link #getFactory(Function)} 调用.
     * <p>由于 ServiceLoader 线程不安全, 所以通过 synchronized 保证其安全性.
     * 不通过 static 块进行初始化的原因是因为担心发生异常导致无法继续执行
     * (除非必要, 否则不要使用 static 执行可能会发生异常的代码.).
     */
    private synchronized static void loadFactory() {
        if (FACTORY_LIST.size() != 0) {
            return;
        }
        final ServiceLoader<CacheStoreFactory> factoryLoader = ServiceLoader.load(CacheStoreFactory.class);
        try {
            for (CacheStoreFactory factory : factoryLoader) {
                FactoryInfo info;
                try {
                    info = new FactoryInfo(factory.getClass());
                    FACTORY_INFO_MAP.put(factory, info);
                } catch (IllegalArgumentException e) {
                    log.warn("Factory {} 加载失败: {}", factory.getClass().getName(), e.getMessage());
                    continue;
                }
                FACTORY_LIST.add(factory);
                log.info("Factory {} 已加载(优先级: {}, 实现类: {}).",
                        info.getFactoryName(),
                        info.getFactoryPriority(),
                        factory.getClass().getName());
            }
            FACTORY_LIST.sort(new PriorityComparator());
        } catch (Error error) {
            // 防止发生 Error 又不输出到日志导致玄学问题难以排查.
            log.error("加载 CacheStoreFactory 时发生严重错误.", error);
            throw error;
        }
    }

    /**
     * 优先级排序器.
     */
    private final static class PriorityComparator implements Comparator<CacheStoreFactory> {
        @Override
        public int compare(CacheStoreFactory o1, CacheStoreFactory o2) {
            FactoryInfo info1 = Objects.requireNonNull(FACTORY_INFO_MAP.get(o1));
            FactoryInfo info2 = Objects.requireNonNull(FACTORY_INFO_MAP.get(o2));
            return info2.getFactoryPriority() - info1.getFactoryPriority();
        }
    }

    /**
     * 获取一个当前可用的高优先级 Factory 对象.
     * @return 返回可用的高优先级 Factory 对象.
     */
    private static <R extends CacheStore<?>> R getFactory(Function<CacheStoreFactory, R> function) throws NoSuchFactoryException {
        if (FACTORY_LIST.size() == 0) {
            loadFactory();
        }
        Iterator<CacheStoreFactory> iterator = FACTORY_LIST.iterator();
        while (iterator.hasNext()) {
            CacheStoreFactory factory = iterator.next();
            FactoryInfo info = FACTORY_INFO_MAP.get(factory);
            try {
                if (factory.canGetCacheStore()) {
                    log.debug("CacheStoreFactory {} 可用(优先级: {}).", info.getFactoryName(), info.getFactoryPriority());
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
                    throw new NoSuchFactoryException(new GetCacheStoreException("CacheStoreFactory " + info.getFactoryName() +
                            " (" + factory.getClass().getName() + ") 创建 CacheStore 时发生异常.", e));
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
     * 获取单项缓存存储容器.
     * @param identify 缓存容器标识.
     * @param converter 类型转换器.
     * @param <V> 值类型.
     * @return 返回新的存储容器, 与其他容器互不干扰.
     * @throws GetCacheStoreException 当无法获取可用的 CacheStore 时抛出.
     */
    public static <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) {
        try {
            return getFactory(factory -> {
                SingleCacheStore<V> singleCacheStoreInstance = factory.newSingleCacheStore(identify, converter);
                if (singleCacheStoreInstance == null) {
                    throw new GetCacheStoreException("Factory " + factory.getClass().getName() + " 返回 null.");
                }
                return singleCacheStoreInstance;
            });
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
    public static <E> ListCacheStore<E> newListCacheStore(String identify, StringConverter<E> converter) {
        try {
            return getFactory(factory -> {
                ListCacheStore<E> listCacheStoreInstance = factory.newListCacheStore(identify, converter);
                if (listCacheStoreInstance == null) {
                    throw new GetCacheStoreException("Factory " + factory.getClass().getName() + " 返回 null.");
                }
                return listCacheStoreInstance;
            });
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
    public static <E> SetCacheStore<E> newSetCacheStore(String identify, StringConverter<E> converter) {
        try {
            return getFactory(factory -> {
                SetCacheStore<E> setCacheStoreInstance = factory.newSetCacheStore(identify, converter);
                if (setCacheStoreInstance == null) {
                    throw new GetCacheStoreException("Factory " + factory.getClass().getName() + " 返回 null.");
                }
                return setCacheStoreInstance;
            });
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
    public static <V> MapCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter) {
        try {
            return getFactory(factory -> {
                MapCacheStore<V> mapCacheStoreInstance = factory.newMapCacheStore(identify, converter);
                if (mapCacheStoreInstance == null) {
                    throw new GetCacheStoreException("Factory " + factory.getClass().getName() + " 返回 null.");
                }
                return mapCacheStoreInstance;
            });
        } catch (NoSuchFactoryException e) {
            throw new GetCacheStoreException("无可用的 Factory.", e);
        }
    }

}
