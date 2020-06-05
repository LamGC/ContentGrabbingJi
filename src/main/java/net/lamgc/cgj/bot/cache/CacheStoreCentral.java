package net.lamgc.cgj.bot.cache;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lamgc.cgj.bot.SettingProperties;
import net.lamgc.cgj.bot.boot.BotGlobal;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivSearchBuilder;
import net.lamgc.cgj.pixiv.PixivURL;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public final class CacheStoreCentral {

    private CacheStoreCentral() {}

    private final static Logger log = LoggerFactory.getLogger(CacheStoreCentral.class);

    /**
     * 作品信息缓存 - 不过期
     */
    private final static CacheStore<JsonElement> illustInfoCache =
            new JsonRedisCacheStore(BotGlobal.getGlobal().getRedisServer(),
                    "illustInfo", BotGlobal.getGlobal().getGson());

    /**
     * 作品信息预加载数据 - 有效期 2 小时, 本地缓存有效期1 ± 0.25
     */
    private final static CacheStore<JsonElement> illustPreLoadDataCache =
            CacheStoreUtils.hashLocalHotDataStore(
                    new JsonRedisCacheStore(BotGlobal.getGlobal().getRedisServer(),
                            "illustPreLoadData", BotGlobal.getGlobal().getGson()),
                    3600000, 900000);
    /**
     * 搜索内容缓存, 有效期 2 小时
     */
    private final static CacheStore<JsonElement> searchBodyCache =
            new JsonRedisCacheStore(BotGlobal.getGlobal().getRedisServer(),
                    "searchBody", BotGlobal.getGlobal().getGson());

    /**
     * 排行榜缓存, 不过期
     */
    private final static CacheStore<List<JsonObject>> rankingCache =
            new JsonObjectRedisListCacheStore(BotGlobal.getGlobal().getRedisServer(),
                    "ranking", BotGlobal.getGlobal().getGson());

    /**
     * 作品页面下载链接缓存 - 不过期
     */
    private final static CacheStore<List<String>> pagesCache =
            new StringListRedisCacheStore(BotGlobal.getGlobal().getRedisServer(), "imagePages");

    /**
     * 清空所有缓存
     */
    public static void clearCache() {
        illustInfoCache.clear();
        illustPreLoadDataCache.clear();
        searchBodyCache.clear();
        rankingCache.clear();
        pagesCache.clear();
    }

    /**
     * 获取作品信息
     * @param illustId 作品Id
     * @param flushCache 强制刷新缓存
     * @return 返回作品信息
     * @throws IOException 当Http请求发生异常时抛出
     * @throws NoSuchElementException 当作品未找到时抛出
     */
    public static JsonObject getIllustInfo(int illustId, boolean flushCache)
            throws IOException, NoSuchElementException {
        String illustIdStr = buildSyncKey(Integer.toString(illustId));
        JsonObject illustInfoObj = null;
        if (!illustInfoCache.exists(illustIdStr) || flushCache) {
            synchronized (illustIdStr) {
                if (!illustInfoCache.exists(illustIdStr) || flushCache) {
                    illustInfoObj = BotGlobal.getGlobal().getPixivDownload().getIllustInfoByIllustId(illustId);
                    illustInfoCache.update(illustIdStr, illustInfoObj, null);
                }
            }
        }

        if(Objects.isNull(illustInfoObj)) {
            illustInfoObj = illustInfoCache.getCache(illustIdStr).getAsJsonObject();
            log.debug("作品Id {} IllustInfo缓存命中.", illustId);
        }
        return illustInfoObj;
    }

    /**
     * 获取作品预加载数据.
     * 可以获取作品的一些与用户相关的信息
     * @param illustId 作品Id
     * @param flushCache 是否刷新缓存
     * @return 成功返回JsonObject对象
     * @throws IOException 当Http请求处理发生异常时抛出
     */
    public static JsonObject getIllustPreLoadData(int illustId, boolean flushCache) throws IOException {
        String illustIdStr = buildSyncKey(Integer.toString(illustId));
        JsonObject result = null;
        if (!illustPreLoadDataCache.exists(illustIdStr) || flushCache) {
            synchronized (illustIdStr) {
                if (!illustPreLoadDataCache.exists(illustIdStr) || flushCache) {
                    log.debug("IllustId {} 缓存失效, 正在更新...", illustId);
                    JsonObject preLoadDataObj = BotGlobal.getGlobal().getPixivDownload()
                            .getIllustPreLoadDataById(illustId)
                            .getAsJsonObject("illust")
                            .getAsJsonObject(Integer.toString(illustId));

                    long expire = 7200 * 1000;
                    String propValue = SettingProperties.
                            getProperty(SettingProperties.GLOBAL, "cache.illustPreLoadData.expire", "7200000");
                    log.debug("PreLoadData有效时间设定: {}", propValue);
                    try {
                        expire = Long.parseLong(propValue);
                    } catch (Exception e) {
                        log.warn("全局配置项 \"{}\" 值非法, 已使用默认值: {}", propValue, expire);
                    }

                    result = preLoadDataObj;
                    illustPreLoadDataCache.update(illustIdStr, preLoadDataObj, expire);
                    log.debug("作品Id {} preLoadData缓存已更新(有效时间: {})", illustId, expire);
                }
            }
        }

        if(Objects.isNull(result)) {
            result = illustPreLoadDataCache.getCache(illustIdStr).getAsJsonObject();
            log.debug("作品Id {} PreLoadData缓存命中.", illustId);
        }
        return result;
    }

    public static List<String> getIllustPages(int illustId, PixivDownload.PageQuality quality, boolean flushCache)
            throws IOException {
        String pagesSign = buildSyncKey(Integer.toString(illustId), ".", quality.name());
        List<String> result = null;
        if (!pagesCache.exists(pagesSign) || flushCache) {
            synchronized (pagesSign) {
                if (!pagesCache.exists(pagesSign) || flushCache) {
                    List<String> linkList = PixivDownload
                            .getIllustAllPageDownload(BotGlobal.getGlobal().getPixivDownload().getHttpClient(),
                                    BotGlobal.getGlobal().getPixivDownload().getCookieStore(), illustId, quality);
                    result = linkList;
                    pagesCache.update(pagesSign, linkList, null);
                }
            }
        }

        if(Objects.isNull(result)) {
            result = pagesCache.getCache(pagesSign);
            log.debug("作品Id {} Pages缓存命中.", illustId);
        }
        return result;
    }

    private final static Random expireTimeFloatRandom = new Random();
    /**
     * 获取排行榜
     * @param contentType 排行榜类型
     * @param mode 排行榜模式
     * @param queryDate 查询时间
     * @param start 开始排名, 从1开始
     * @param range 取范围
     * @param flushCache 是否强制刷新缓存
     * @return 成功返回有值List, 失败且无异常返回空
     * @throws IOException 获取异常时抛出
     */
    public static List<JsonObject> getRankingInfoByCache(PixivURL.RankingContentType contentType,
                                                         PixivURL.RankingMode mode,
                                                         Date queryDate, int start, int range, boolean flushCache)
            throws IOException {
        if(!contentType.isSupportedMode(mode)) {
            log.warn("试图获取不支持的排行榜类型已拒绝.(ContentType: {}, RankingMode: {})", contentType.name(), mode.name());
            if(log.isDebugEnabled()) {
                try {
                    Thread.dumpStack();
                } catch(Exception e) {
                    log.debug("本次非法请求的堆栈信息如下: \n{}", Throwables.getStackTraceAsString(e));
                }
            }
            return new ArrayList<>(0);
        }

        String date = new SimpleDateFormat("yyyyMMdd").format(queryDate);
        String requestSign = buildSyncKey(contentType.name(), ".", mode.name(), ".", date);
        List<JsonObject> result = null;
        if(!rankingCache.exists(requestSign) || flushCache) {
            synchronized(requestSign) {
                if(!rankingCache.exists(requestSign) || flushCache) {
                    log.debug("Ranking缓存失效, 正在更新...(RequestSign: {})", requestSign);
                    List<JsonObject> rankingResult = BotGlobal.getGlobal().getPixivDownload()
                            .getRanking(contentType, mode, queryDate, 1, 500);
                    long expireTime = 0;
                    if(rankingResult.size() == 0) {
                        expireTime = 5400000 + expireTimeFloatRandom.nextInt(1800000);
                        log.warn("数据获取失败, 将设置浮动有效时间以准备下次更新. (ExpireTime: {}ms)", expireTime);
                    }
                    result = new ArrayList<>(rankingResult).subList(start - 1, start + range - 1);
                    rankingCache.update(requestSign, rankingResult, expireTime);
                    log.debug("Ranking缓存更新完成.(RequestSign: {})", requestSign);
                }
            }
        }

        if (Objects.isNull(result)) {
            result = rankingCache.getCache(requestSign, start - 1, range);
            log.debug("RequestSign [{}] 缓存命中.", requestSign);
        }
        log.debug("Result-Length: {}", result.size());
        return PixivDownload.getRanking(result, start - 1, range);
    }

    /**
     * 获取搜索结果
     * @param content 搜索内容
     * @param type 类型
     * @param area 范围
     * @param includeKeywords 包含关键词
     * @param excludeKeywords 排除关键词
     * @param contentOption 内容类型
     * @return 返回完整搜索结果
     * @throws IOException 当请求发生异常, 或接口返回异常信息时抛出.
     */
    public static JsonObject getSearchBody(
            String content,
            String type,
            String area,
            String includeKeywords,
            String excludeKeywords,
            String contentOption) throws IOException {
        PixivSearchBuilder searchBuilder = new PixivSearchBuilder(Strings.isNullOrEmpty(content) ? "" : content);
        if (type != null) {
            try {
                searchBuilder.setSearchType(PixivSearchBuilder.SearchType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchType: {}", type);
            }
        }
        if (area != null) {
            try {
                searchBuilder.setSearchArea(PixivSearchBuilder.SearchArea.valueOf(area));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchArea: {}", area);
            }
        }
        if (contentOption != null) {
            try {
                searchBuilder.setSearchContentOption(PixivSearchBuilder.SearchContentOption.valueOf(contentOption));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchContentOption: {}", contentOption);
            }
        }

        if (!Strings.isNullOrEmpty(includeKeywords)) {
            for (String keyword : includeKeywords.split(";")) {
                searchBuilder.removeExcludeKeyword(keyword.trim());
                searchBuilder.addIncludeKeyword(keyword.trim());
                log.debug("已添加关键字: {}", keyword);
            }
        }
        if (!Strings.isNullOrEmpty(excludeKeywords)) {
            for (String keyword : excludeKeywords.split(";")) {
                searchBuilder.removeIncludeKeyword(keyword.trim());
                searchBuilder.addExcludeKeyword(keyword.trim());
                log.debug("已添加排除关键字: {}", keyword);
            }
        }

        log.info("正在搜索作品, 条件: {}", searchBuilder.getSearchCondition());

        String requestUrl = searchBuilder.buildURL().intern();
        log.debug("RequestUrl: {}", requestUrl);
        JsonObject resultBody = null;
        if(!searchBodyCache.exists(requestUrl)) {
            synchronized (requestUrl) {
                if (!searchBodyCache.exists(requestUrl)) {
                    log.debug("searchBody缓存失效, 正在更新...");
                    JsonObject jsonObject;
                    HttpGet httpGetRequest = BotGlobal.getGlobal().getPixivDownload().
                            createHttpGetRequest(requestUrl);
                    HttpResponse response = BotGlobal.getGlobal().getPixivDownload().
                            getHttpClient().execute(httpGetRequest);

                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    log.debug("ResponseBody: {}", responseBody);
                    jsonObject = BotGlobal.getGlobal().getGson().fromJson(responseBody, JsonObject.class);

                    if (jsonObject.get("error").getAsBoolean()) {
                        log.error("接口请求错误, 错误信息: {}", jsonObject.get("message").getAsString());
                        throw new IOException("Interface Request Error: " + jsonObject.get("message").getAsString());
                    }

                    long expire = 7200 * 1000;
                    String propValue = SettingProperties
                            .getProperty(SettingProperties.GLOBAL, "cache.searchBody.expire", "7200000");
                    try {
                        expire = Long.parseLong(propValue);
                    } catch (Exception e) {
                        log.warn("全局配置项 \"{}\" 值非法, 已使用默认值: {}", propValue, expire);
                    }
                    resultBody = jsonObject.getAsJsonObject().getAsJsonObject("body");
                    searchBodyCache.update(requestUrl, jsonObject, expire);
                    log.debug("searchBody缓存已更新(有效时间: {})", expire);
                } else {
                    log.debug("搜索缓存命中.");
                }
            }
        } else {
            log.debug("搜索缓存命中.");
        }

        if(Objects.isNull(resultBody)) {
            resultBody = searchBodyCache.getCache(requestUrl).getAsJsonObject().getAsJsonObject("body");
        }
        return resultBody;
    }

    /**
     * 合并String并存取到常量池, 以保证对象一致
     * @param keys String对象
     * @return 合并后, 如果常量池存在合并后的结果, 则返回常量池中的对象, 否则存入常量池后返回.
     */
    private static String buildSyncKey(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String string : keys) {
            sb.append(string);
        }
        return sb.toString().intern();
    }
}
