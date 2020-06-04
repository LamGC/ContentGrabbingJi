package net.lamgc.cgj.bot.boot;

import com.google.common.base.Strings;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

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

    private final HttpHost proxy;

    private CookieStore cookieStore;

    private BotGlobal() {
        this.redisUri = URI.create("redis://" + System.getProperty("cgj.redisAddress"));
        this.redisServer = new JedisPool(
                getRedisUri().getHost(),
                getRedisUri().getPort() == -1 ? 6379 : getRedisUri().getPort());
        String dataStoreDirPath = System.getProperty("cgj.botDataDir");
        this.dataStoreDir = new File((!dataStoreDirPath.endsWith("/") || !dataStoreDirPath.endsWith("\\")) ?
                dataStoreDirPath + System.getProperty("file.separator") : dataStoreDirPath);

        String proxyAddress = System.getProperty("cgj.proxy");
        HttpHost temp = null;
        if(!Strings.isNullOrEmpty(proxyAddress)) {
            try {
                URL proxyUrl = new URL(proxyAddress);
                temp = new HttpHost(proxyUrl.getHost(), proxyUrl.getPort());
                log.info("已启用Http协议代理：{}", temp.toHostString());
            } catch (MalformedURLException e) {
                log.error("Proxy地址解析失败, 代理将不会启用.", e);
            }
        }
        this.proxy = temp;
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

    public HttpHost getProxy() {
        return proxy;
    }


    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }
}
