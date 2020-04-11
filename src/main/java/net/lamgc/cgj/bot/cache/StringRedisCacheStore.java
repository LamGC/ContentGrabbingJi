package net.lamgc.cgj.bot.cache;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

public class StringRedisCacheStore extends RedisPoolCacheStore<String> {
    public StringRedisCacheStore(URI redisServerUri, String prefix) {
        super(redisServerUri, prefix);
    }

    public StringRedisCacheStore(URI redisServerUri, JedisPoolConfig config, int timeout, String password, String prefix) {
        super(redisServerUri, config, timeout, password, prefix);
    }

    public StringRedisCacheStore(JedisPool pool, String keyPrefix) {
        super(pool, keyPrefix);
    }

    @Override
    protected String parse(String dataObj) {
        return dataObj;
    }

    @Override
    protected String analysis(String dataStr) {
        return dataStr;
    }
}
