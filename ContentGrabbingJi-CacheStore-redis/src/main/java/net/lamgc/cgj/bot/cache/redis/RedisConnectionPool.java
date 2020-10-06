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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * 统一的 Redis 连接池.
 * @author LamGC
 */
class RedisConnectionPool {

    private final static Logger log = LoggerFactory.getLogger(RedisConnectionPool.class);
    private final static AtomicReference<JedisPool> POOL = new AtomicReference<>();

    public static synchronized void reconnectRedis() {
        JedisPool jedisPool = POOL.get();
        if (jedisPool != null && !jedisPool.isClosed()) {
            return;
        }
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        jedisPool = new JedisPool(config);
        POOL.set(jedisPool);
    }

    /**
     * 获取一个 Jedis 对象.
     * <p>注意, 需回收 Jedis 对象, 否则可能会耗尽连接池导致后续操作受到影响.
     * @return 返回可用的 Jedis 连接.
     */
    public static Jedis getConnection() {
        JedisPool pool = POOL.get();
        if (pool == null || pool.isClosed()) {
            reconnectRedis();
            pool = POOL.get();
            if (pool == null) {
                throw new IllegalStateException("Redis connection lost");
            }
        }
        return pool.getResource();
    }

    /**
     * 执行 Redis 操作并返回相关值.
     * <p>本方法会自动回收 Jedis.
     * @param function 待运行的操作.
     * @param <R> 返回值类型.
     * @return 返回 function 返回的内容.
     */
    public static <R> R executeRedis(Function<Jedis, R> function) {
        try (Jedis jedis = getConnection()) {
            return function.apply(jedis);
        }
    }

    /**
     * 检查 Redis 连接池是否有可用的资源.
     * @return 如果连接池依然活跃, 返回 true.
     */
    public static boolean available() {
        JedisPool jedisPool = POOL.get();
        if (jedisPool == null || jedisPool.isClosed()) {
            reconnectRedis();
            jedisPool = POOL.get();
            if (jedisPool == null || jedisPool.isClosed()) {
                return false;
            }
        }
        if (jedisPool.getNumIdle() == 0) {
            try (Jedis jedis = jedisPool.getResource()) {
                return "pong".equalsIgnoreCase(jedis.ping());
            } catch (Exception e) {
                log.error("Redis 连接测试时发生异常", e);
            }
            return false;
        }
        return true;
    }

}
