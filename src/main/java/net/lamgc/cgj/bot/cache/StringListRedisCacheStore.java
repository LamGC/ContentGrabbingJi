package net.lamgc.cgj.bot.cache;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

public class StringListRedisCacheStore extends RedisListCacheStore<String> {
    public StringListRedisCacheStore(URI redisServerUri, String prefix) {
        super(redisServerUri, prefix);
    }

    public StringListRedisCacheStore(URI redisServerUri, JedisPoolConfig config, int timeout, String password, String prefix) {
        super(redisServerUri, config, timeout, password, prefix);
    }

    public StringListRedisCacheStore(JedisPool pool, String prefix) {
        super(pool, prefix);
    }

    @Override
    public String parseData(String dataObj) {
        return dataObj;
    }

    @Override
    public String analysisData(String str) {
        return str;
    }
}
