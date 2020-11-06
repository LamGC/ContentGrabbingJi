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

import com.google.common.base.Strings;
import net.lamgc.cgj.bot.cache.*;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.exception.GetCacheStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 *
 * @author LamGC
 */
@Factory(name = "Redis", priority = FactoryPriority.PRIORITY_HIGHER, source = CacheStoreSource.REMOTE)
public class RedisCacheStoreFactory implements CacheStoreFactory {

    private final static Logger log = LoggerFactory.getLogger(RedisCacheStoreFactory.class);

    private final static String PROP_HOST = "redis.host";
    private final static String PROP_PORT = "redis.port";
    private final static String PROP_USE_SSL = "redis.useSSL";
    private final static String PROP_USERNAME = "redis.username";
    private final static String PROP_PASSWORD = "redis.password";
    private final static String PROP_DATABASE = "redis.databaseId";
    private final static String PROP_CLIENT_NAME = "redis.clientName";

    private final RedisConnectionPool connectionPool = new RedisConnectionPool();

    @Override
    public void initial(File dataDirectory) {
        final File propertiesFile = new File(dataDirectory, "redis.properties");
        if (!propertiesFile.exists()) {
            log.warn("未找到 Redis 配置文件, 使用默认配置.");
            return;
        } else if (!propertiesFile.isFile()) {
            log.warn("Redis 配置文件不是一个文件, 使用默认配置.");
            return;
        }
        Properties properties = new Properties();
        try (Reader propertiesReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(propertiesFile), StandardCharsets.UTF_8))) {
            properties.load(propertiesReader);
        } catch (IOException e) {
            log.error("读取 Redis 配置文件时发生异常", e);
        }
        try {
            String queryString = "/?" + "ssl=" + properties.getProperty(PROP_USE_SSL, "false") + '&' +
                    "user=" + Strings.nullToEmpty(properties.getProperty(PROP_USERNAME)) + '&' +
                    "passwd=" + Strings.nullToEmpty(properties.getProperty(PROP_PASSWORD)) + '&' +
                    "database=" + properties.getProperty(PROP_DATABASE, "0") + '&' +
                    "clientName=" + Strings.nullToEmpty(properties.getProperty(PROP_CLIENT_NAME));
            URL url = new URL("redis",
                    properties.getProperty(PROP_HOST, "localhost"),
                    Integer.parseInt(properties.getProperty(PROP_PORT, "6379")),
                    queryString);

            connectionPool.setConnectionUrl(url);
        } catch (MalformedURLException e) {
            log.error("构造连接 URL 时发生异常", e);
        }
    }

    @Override
    public <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) {
        return new RedisSingleCacheStore<>(connectionPool, identify, converter);
    }

    @Override
    public <E> ListCacheStore<E> newListCacheStore(String identify, StringConverter<E> converter) {
        throw new GetCacheStoreException("No corresponding implementation");
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
