package net.lamgc.cgj.bot.cache;

import com.google.common.base.Strings;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.net.URI;
import java.util.Date;
import java.util.Objects;

public abstract class RedisPoolCacheStore<T> implements CacheStore<T> {

    private final JedisPool jedisPool;
    private final String keyPrefix;
    private final Logger log;

    public RedisPoolCacheStore(URI redisServerUri, String prefix) {
        this(redisServerUri, null, 0, null, prefix);
    }

    public RedisPoolCacheStore(URI redisServerUri, JedisPoolConfig config, int timeout, String password, String prefix) {
        jedisPool = new JedisPool(config == null ? new GenericObjectPoolConfig<JedisPool>() : config, redisServerUri.getHost(),
                redisServerUri.getPort() <= 0 ? 6379 : redisServerUri.getPort(),
                timeout <= 0 ? Protocol.DEFAULT_TIMEOUT : timeout, password);
        log = LoggerFactory.getLogger(this.getClass().getSimpleName() + "@" + Integer.toHexString(jedisPool.hashCode()));
        if(prefix != null) {
            keyPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
        } else {
            keyPrefix = "";
        }
    }

    public RedisPoolCacheStore(JedisPool pool, String keyPrefix) {
        jedisPool = Objects.requireNonNull(pool);
        if(jedisPool.isClosed()) {
            throw new IllegalStateException("JedisPool is closed");
        }
        log = LoggerFactory.getLogger(this.getClass().getSimpleName() + "@" + Integer.toHexString(jedisPool.hashCode()));
        if(!Strings.isNullOrEmpty(keyPrefix)) {
            this.keyPrefix = keyPrefix.endsWith(".") ? keyPrefix : keyPrefix + ".";
        } else {
            this.keyPrefix = "";
        }
    }

    @Override
    public void update(String key, T value, Date expire) {
        Jedis jedis = jedisPool.getResource();
        Transaction multi = jedis.multi();
        multi.set(keyPrefix + key, parse(value));
        if(expire != null) {
            multi.expireAt(keyPrefix + key, expire.getTime());
            log.debug("已设置Key {} 的过期时间(Expire: {})", key, expire.getTime());
        }
        multi.exec();
        jedis.close();
    }

    @Override
    public T getCache(String key) {
        Jedis jedis = jedisPool.getResource();
        T result = analysis(jedis.get(keyPrefix + key));
        jedis.close();
        return result;
    }

    @Override
    public boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        boolean result = jedis.exists(keyPrefix + key);
        jedis.close();
        return result;
    }

    @Override
    public boolean exists(String key, Date date) {
        return exists(key);
    }

    @Override
    public boolean clear() {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.flushDB();
        jedis.close();
        log.info("flushDB返回结果: {}", result);
        return true;
    }

    /**
     * 转换方法
     * @param dataObj 原数据
     * @return 文本型数据
     */
    protected abstract String parse(T dataObj);

    /**
     * 将String数据转换成指定类型的对象
     * @param dataStr String数据
     * @return 泛型指定类型的对象
     */
    protected abstract T analysis(String dataStr);

    @Override
    public boolean supportedPersistence() {
        return true;
    }
}
