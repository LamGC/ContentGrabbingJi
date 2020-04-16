package net.lamgc.cgj.bot.cache;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class RedisListCacheStore<T> extends RedisPoolCacheStore<List<T>> {

    private final String keyPrefix;

    public RedisListCacheStore(URI redisServerUri, String prefix) {
        super(redisServerUri, prefix);
        keyPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
    }

    public RedisListCacheStore(URI redisServerUri, JedisPoolConfig config, int timeout, String password, String prefix) {
        super(redisServerUri, config, timeout, password, prefix);
        keyPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
    }

    public RedisListCacheStore(JedisPool pool, String prefix) {
        super(pool, prefix);
        keyPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
    }

    @Override
    public void update(String key, List<T> value, Date expire) {
        executeJedisCommand(jedis -> {
            String[] arr = new String[value.size()];
            for (int i = 0; i < value.size(); i++) {
                arr[i] = parseData(value.get(i));
            }
            jedis.rpush(keyPrefix + key, arr);
            if(expire != null) {
                jedis.pexpireAt(keyPrefix + key, expire.getTime());
            }
        });
    }

    @Override
    public List<T> getCache(String key) {
        return getCache(key, 0, length(key));
    }

    @Override
    public List<T> getCache(String key, long index, long length) {
        return executeJedisCommand(jedis -> {
            List<String> strings = jedis.lrange(keyPrefix + key, Math.max(0, index), Math.max(0, index + length - 1));
            List<T> results = new ArrayList<>(strings.size());
            strings.forEach(item -> results.add(analysisData(item)));
            return results;
        });
    }

    @Override
    public long length(String key) {
        return executeJedisCommand(jedis -> {
            return jedis.llen(keyPrefix + key);
        });
    }

    @Override
    protected String parse(List<T> dataObj) {
        return null;
    }

    @Override
    protected List<T> analysis(String dataStr) {
        return null;
    }

    /**
     * 将数据转换成String
     * @param dataObj 数据对象
     * @return 转换结果
     */
    public abstract String parseData(T dataObj);

    /**
     * 将String转换成数据
     * @param str String对象
     * @return 转换结果
     */
    public abstract T analysisData(String str);

    @Override
    public boolean supportedList() {
        return true;
    }
}
