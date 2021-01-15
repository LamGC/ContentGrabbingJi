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

import com.google.gson.Gson;
import net.lamgc.cgj.bot.cache.*;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.exception.GetCacheStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author LamGC
 */
@Factory(name = "Redis", priority = FactoryPriority.PRIORITY_HIGHER, source = CacheStoreSource.REMOTE)
public class RedisCacheStoreFactory implements CacheStoreFactory {

    private final static Logger log = LoggerFactory.getLogger(RedisCacheStoreFactory.class);

    private final RedisConnectionPool connectionPool = new RedisConnectionPool();

    @Override
    public void initial(File dataDirectory) {
        final File propertiesFile = new File(dataDirectory, RedisUtils.PROPERTIES_FILE_NAME);
        if (!propertiesFile.exists()) {
            log.warn("未找到 Redis 配置文件, 使用默认配置.");
            return;
        } else if (!propertiesFile.isFile()) {
            log.warn("Redis 配置文件不是一个文件, 使用默认配置.");
            return;
        }
        try (Reader propertiesReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(propertiesFile), StandardCharsets.UTF_8))) {
            RedisConnectionProperties properties = new Gson()
                    .fromJson(propertiesReader, RedisConnectionProperties.class);
            connectionPool.setConnectionProperties(properties);
            log.debug("Redis 配置文件已成功读取: {}", properties);
        } catch (IOException e) {
            log.error("读取 Redis 配置文件时发生异常, 将使用默认配置连接 Redis.", e);
        }
    }

    @Override
    public <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) {
        return new RedisSingleCacheStore<>(connectionPool, identify, converter);
    }

    @Override
    public <E> ListCacheStore<E> newListCacheStore(String identify, StringConverter<E> converter) {
        return new RedisListCacheStore<>(connectionPool, identify, converter);
    }

    @Override
    public <E> SetCacheStore<E> newSetCacheStore(String identify, StringConverter<E> converter) {
        throw new GetCacheStoreException("No corresponding implementation");
    }

    @Override
    public <V> MapCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter) {
        return new RedisMapCacheStore<>(connectionPool, identify, converter);
    }

    @Override
    public boolean canGetCacheStore() {
        return connectionPool.available();
    }
}
