package net.lamgc.cgj.bot.boot;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.lamgc.cgj.pixiv.PixivDownload;
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

    private final static Logger log = LoggerFactory.getLogger(BotGlobal.class);

    private final static BotGlobal instance = new BotGlobal();

    public static BotGlobal getGlobal() {
        if(instance == null) {
            throw new IllegalStateException("");
        }
        return instance;
    }

    private final URI redisUri;

    /**
     * 所有缓存共用的JedisPool
     */
    private final JedisPool redisServer;

    private final File dataStoreDir;

    private final HttpHost proxy;

    private CookieStore cookieStore;

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    private PixivDownload pixivDownload;

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
                log.info("已启用代理：{}", temp.toHostString());
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
        if(pixivDownload == null) {
            throw new IllegalStateException("CookieStore needs to be set before PixivDownload can be obtained");
        }
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        if(this.cookieStore != null) {
            throw new IllegalStateException("CookieStore set");
        }
        this.cookieStore = cookieStore;
        this.pixivDownload =
                new PixivDownload(cookieStore, proxy);
    }

    public Gson getGson() {
        return gson;
    }

    public PixivDownload getPixivDownload() {
        return pixivDownload;
    }
}
