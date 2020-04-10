package net.lamgc.cgj.bot.cache;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.URI;
import java.util.Date;

public abstract class RedisCacheStore<T> implements CacheStore<T> {

    private final Jedis jedis;
    private final Logger log;
    private final String keyPrefix;

    public RedisCacheStore(URI redisServerUri, String prefix) {
        this(redisServerUri, null, prefix);
    }

    /**
     * 创建一个Redis缓存数据库对象
     * @param redisServerUri 数据库链接
     * @param password 登录密码(如果有)
     * @throws JedisConnectionException 当连接失败时抛出
     */
    public RedisCacheStore(URI redisServerUri, String password, String prefix) throws JedisConnectionException {
        this.jedis = new Jedis(redisServerUri.getHost(), redisServerUri.getPort() <= 0 ? 6379 : redisServerUri.getPort());
        log = LoggerFactory.getLogger(this.getClass().getSimpleName() + "@" + Integer.toHexString(jedis.hashCode()));
        log.info("Redis数据库连接状态: {}", jedis.ping());
        if(password != null) {
            this.jedis.auth(password);
        }
        if(!Strings.isNullOrEmpty(prefix)) {
            keyPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
        } else {
            keyPrefix = "";
        }
    }

    public void connect() {
        if(!"PONE".equals(jedis.ping())) {
            jedis.connect();
        }
    }

    @Override
    public void update(String key, T value, Date expire) {
        Transaction multi = jedis.multi();
        multi.set(keyPrefix + key, parse(value));
        if(expire != null) {
            multi.expireAt(keyPrefix + key, expire.getTime());
            log.debug("已设置Key {} 的过期时间(Expire: {})", key, expire.getTime());
        }
        multi.exec();
    }

    @Override
    public T getCache(String key) {
        return analysis(jedis.get(keyPrefix + key));
    }

    @Override
    public boolean exists(String key) {
        return jedis.exists(keyPrefix + key);
    }

    @Override
    public boolean exists(String key, Date date) {
        return exists(key);
    }

    @Override
    public boolean clear() {
        String result = jedis.flushDB();
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
