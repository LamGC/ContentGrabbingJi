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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * 统一的 Redis 连接池.
 * @author LamGC
 */
class RedisConnectionPool {

    private final static Logger log = LoggerFactory.getLogger(RedisConnectionPool.class);

    private final AtomicReference<JedisPool> POOL = new AtomicReference<>();
    private final AtomicReference<URL> CONNECTION_URL = new AtomicReference<>();

    private final Map<LuaScript, String> scriptMap = new HashMap<>();

    public synchronized void setConnectionUrl(URL connectionUrl) {
        if(CONNECTION_URL.get() != null) {
            CONNECTION_URL.set(connectionUrl);
        }
    }

    public synchronized void reconnectRedis() {
        JedisPool jedisPool = POOL.get();
        if (jedisPool != null && !jedisPool.isClosed()) {
            return;
        }
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        URL connectionUrl = CONNECTION_URL.get();
        if (connectionUrl == null) {
            jedisPool = new JedisPool(config);
        } else {
            jedisPool = new JedisPool(config, connectionUrl.getHost(), connectionUrl.getPort(),
                    connectionUrl.getPath().toLowerCase().contains("ssl=true"));
        }
        POOL.set(jedisPool);
        loadScript();
    }

    /**
     * 获取一个 Jedis 对象.
     * <p>注意, 需回收 Jedis 对象, 否则可能会耗尽连接池导致后续操作受到影响.
     * @return 返回可用的 Jedis 连接.
     */
    public Jedis getConnection() {
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
    public <R> R executeRedis(Function<Jedis, R> function) {
        try (Jedis jedis = getConnection()) {
            return function.apply(jedis);
        }
    }

    /**
     * 检查 Redis 连接池是否有可用的资源.
     * @return 如果连接池依然活跃, 返回 true.
     */
    public boolean available() {
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
                return false;
            }
        }
        return true;
    }

    /**
     * 获取指定脚本的 Sha.
     * @param script 脚本.
     * @return 如果存在, 返回 Sha, 否则返回 null.
     */
    public String getScriptSha(LuaScript script) {
        return scriptMap.get(script);
    }

    /**
     * 加载脚本.
     */
    private void loadScript() {
        for (LuaScript script : LuaScript.values()) {
            InputStream scriptStream = this.getClass().
                    getResourceAsStream("/" + LuaScript.PACKAGE_PATH + script.getScriptName() + ".lua");
            if (scriptStream == null) {
                log.warn("脚本 {} 获取失败, 相关操作将无法使用, 请检查缓存组件是否损坏.", script.getScriptName());
                continue;
            }

            String scriptContent;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(scriptStream, StandardCharsets.UTF_8))) {
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
                scriptContent = builder.toString();
            } catch (IOException e) {
                log.error("读取脚本文件时发生异常.(Script: " + script.getScriptName() + ")", e);
                continue;
            }

            try {
                String scriptSha = executeRedis(jedis -> jedis.scriptLoad(scriptContent));
                if (scriptSha != null) {
                    scriptMap.put(script, scriptSha);
                    log.debug("脚本 {} 已成功加载.(Sha: {})", script, scriptSha);
                }
            } catch (Exception e) {
                log.error("加载脚本时发生异常.(Script: " + script.getScriptName() + ")", e);
            }
        }
    }

    /**
     * 执行脚本.
     * @param script Lua 脚本.
     * @param keys 待传入脚本的键列表.
     * @param args 待传入脚本的参数列表.
     * @return 如果成功, 返回脚本所返回的数据, 需根据脚本实际返回转换对象.
     * @throws NullPointerException 当 script 为 {@code null} 时抛出.
     */
    public Object executeScript(final LuaScript script, final List<String> keys, final List<String> args) {
        String scriptSha = this.getScriptSha(Objects.requireNonNull(script));
        if (scriptSha == null) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            log.warn("脚本未加载, 方法 {}() 无法执行(方法存在于Class {}:{}). (所需脚本: {})",
                    stackTraceElements[2].getMethodName(),
                    stackTraceElements[2].getClassName(),
                    stackTraceElements[2].getLineNumber(),
                    script.getScriptName());
            return false;
        }
        return executeRedis(jedis -> {
            List<String> keysList = (keys == null) ? Collections.emptyList() : keys;
            List<String> argsList = (args == null) ? Collections.emptyList() : args;
            return jedis.evalsha(scriptSha, keysList, argsList);
        });
    }


}
