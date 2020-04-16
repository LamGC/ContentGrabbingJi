package net.lamgc.cgj.bot.cache;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

public class JsonObjectRedisListCacheStore extends RedisListCacheStore<JsonObject> {
    private final Gson gson;

    public JsonObjectRedisListCacheStore(URI redisServerUri, String prefix, Gson gson) {
        super(redisServerUri, prefix);
        this.gson = gson;
    }

    public JsonObjectRedisListCacheStore(URI redisServerUri, JedisPoolConfig config, int timeout, String password, String prefix, Gson gson) {
        super(redisServerUri, config, timeout, password, prefix);
        this.gson = gson;
    }

    public JsonObjectRedisListCacheStore(JedisPool pool, String keyPrefix, Gson gson) {
        super(pool, keyPrefix);
        this.gson = gson;
    }

    @Override
    public String parseData(JsonObject dataObj) {
        return gson.toJson(dataObj);
    }

    @Override
    public JsonObject analysisData(String str) {
        return gson.fromJson(str, JsonObject.class);
    }
}
