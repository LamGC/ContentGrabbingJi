package net.lamgc.cgj.bot.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.net.URI;

public final class BotGlobal {

    private final static BotGlobal instance = new BotGlobal();

    public static BotGlobal getGlobal() {
        if(instance == null) {
            throw new IllegalStateException("");
        }
        return instance;
    }

    private final static Logger log = LoggerFactory.getLogger(BotGlobal.class);

    private final URI redisUri;

    /**
     * 所有缓存共用的JedisPool
     */
    private final JedisPool redisServer;

    private final File dataStoreDir;

    private BotGlobal() {
        this.redisUri = URI.create("redis://" + System.getProperty("cgj.redisAddress"));
        this.redisServer = new JedisPool(
                getRedisUri().getHost(),
                getRedisUri().getPort() == -1 ? 6379 : getRedisUri().getPort());
        String dataStoreDirPath = System.getProperty("cgj.botDataDir");
        this.dataStoreDir = new File((!dataStoreDirPath.endsWith("/") || !dataStoreDirPath.endsWith("\\")) ?
                dataStoreDirPath + System.getProperty("file.separator") : dataStoreDirPath);
    }

    public URI getRedisUri() {
        return redisUri;
    }

    public File getDataStoreDir() {
        if(!dataStoreDir.exists() && !dataStoreDir.mkdirs()) {
            log.error("DataStoreDir 创建失败, 数据存储可能失效!");
        }
        return dataStoreDir;
    }

    public JedisPool getRedisServer() {
        return redisServer;
    }
}
