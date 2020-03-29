package net.lamgc.cgj;

import com.google.common.base.Strings;
import com.google.gson.*;
import io.netty.handler.codec.http.HttpHeaderNames;
import net.lamgc.cgj.cache.*;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivSearchBuilder;
import net.lamgc.cgj.pixiv.PixivURL;
import net.lamgc.utils.base.runner.Argument;
import net.lamgc.utils.base.runner.Command;
import net.lamgc.utils.event.EventExecutor;
import net.lz1998.cq.utils.CQCode;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CQProcess {

    private final static PixivDownload pixivDownload = new PixivDownload(Main.cookieStore, Main.proxy);

    private final static Logger log = LoggerFactory.getLogger("CQProcess");

    private final static File imageStoreDir = new File(System.getProperty("cgj.cqRootDir"), "data/image/cgj/");

    private final static Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    private final static Hashtable<String, File> imageCache = new Hashtable<>();

    private final static CacheStore<JsonObject> illustInfoCache = new LocalHashCacheStore<>();

    private final static CacheStore<JsonObject> illustPreLoadDataCache = new LocalHashCacheStore<>();

    private final static CacheStore<JsonObject> searchBodyCache = new LocalHashCacheStore<>();

    private final static CacheStore<List<String>> pagesCache = new LocalHashCacheStore<>();

    private final static CacheStore<JsonArray> rankingCache = new LocalHashCacheStore<>();

    private final static EventExecutor imageCacheExecutor = new EventExecutor(new ThreadPoolExecutor(
            1,
            (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
            15L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    ));

    static {
        try {
            imageCacheExecutor.addHandler(new ImageCacheHandler());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Command(defaultCommand = true)
    public static String help() {
        StringBuilder helpStrBuilder = new StringBuilder();
        helpStrBuilder.append("CGJ Bot使用指南").append("\n");
        helpStrBuilder.append("目前可用的命令:").append("\n");
        helpStrBuilder.append("\t").append("ranking - 获取今天或指定日期排行榜的前10名作品").append("\n");
        helpStrBuilder.append("\t\t").append("-date - 指定查询日期(年-月-日)").append("\n");
        helpStrBuilder.append("\t").append("search - 搜索指定关键词并显示前10个作品").append("\n");
        helpStrBuilder.append("\t\t").append("-content - 搜索内容").append("\n");
        helpStrBuilder.append("\t").append("artworks - 获取作品的Pixiv页面").append("\n");
        helpStrBuilder.append("\t\t").append("-id - 作品id").append("\n");
        return helpStrBuilder.toString();
    }

    @Command
    public static String ranking(
            @Argument(force = false, name = "date") Date queryTime,
            @Argument(force = false, name = "contentMode", defaultValue = "DAILY") String contentMode
    ) {
        Date queryDate = queryTime;
        if (queryDate == null) {
            queryDate = new Date();
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            log.info("CurrentDate: {}", queryDate);
            gregorianCalendar.setTime(queryDate);
            if (gregorianCalendar.get(Calendar.HOUR_OF_DAY) < 12) {
                gregorianCalendar.add(Calendar.DATE, -2);
            } else {
                gregorianCalendar.add(Calendar.DATE, -1);
            }
            queryDate = gregorianCalendar.getTime();
        }

        PixivURL.RankingMode mode = PixivURL.RankingMode.MODE_DAILY;
        try {
            mode = PixivURL.RankingMode.valueOf("MODE_" + contentMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("无效的RankingMode值: {}", contentMode);
        }

        StringBuilder resultBuilder = new StringBuilder(mode.name() + " - 以下是 ").append(new SimpleDateFormat("yyyy-MM-dd").format(queryDate)).append(" 的Pixiv插画排名榜前十名：\n");
        try {
            int index = 0;
            for (JsonObject rankInfo : getRankingInfoByCache(PixivURL.RankingContentType.TYPE_ILLUST, mode, queryDate, 0, 10)) {
                index++;
                int rank = rankInfo.get("rank").getAsInt();
                int illustId = rankInfo.get("illust_id").getAsInt();
                int authorId = rankInfo.get("user_id").getAsInt();
                String authorName = rankInfo.get("user_name").getAsString();
                String title = rankInfo.get("title").getAsString();
                resultBuilder.append(rank).append(". (id: ").append(illustId).append(") ").append(title)
                        .append("(Author: ").append(authorName).append(",").append(authorId).append(")\n");
                if (index < 4) {
                    resultBuilder.append(getImageById(illustId, PixivDownload.PageQuality.REGULAR, 1)).append("\n");
                }
            }
        } catch (IOException e) {
            log.error("消息处理异常", e);
            return "排名榜获取失败！详情请查看机器人控制台。";
        }
        return resultBuilder.append("如查询当前时间获取到昨天时间，则今日排名榜未更新。").toString();
    }

    @Command(commandName = "userArt")
    public static String userArtworks() {

        return "功能未完成";
    }

    private final static Object searchCacheLock = new Object();
    @Command
    public static String search(@Argument(name = "content") String content,
                                @Argument(name = "type", force = false) String type,
                                @Argument(name = "area", force = false) String area,
                                @Argument(name = "in", force = false) String includeKeywords,
                                @Argument(name = "ex", force = false) String excludeKeywords,
                                @Argument(name = "contentOption", force = false) String contentOption,
                                @Argument(name = "page", force = false, defaultValue = "1") int pagesIndex
    ) throws IOException {
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
                searchBuilder.removeExcludeKeyword(keyword);
                searchBuilder.addIncludeKeyword(keyword);
                log.info("已添加关键字: {}", keyword);
            }
        }
        if (!Strings.isNullOrEmpty(excludeKeywords)) {
            for (String keyword : excludeKeywords.split(";")) {
                searchBuilder.removeIncludeKeyword(keyword);
                searchBuilder.addExcludeKeyword(keyword);
                log.info("已添加排除关键字: {}", keyword);
            }
        }

        log.info("正在搜索作品, 条件: {}", searchBuilder.getSearchCondition());

        String requestUrl = searchBuilder.buildURL();
        log.info("RequestUrl: {}", requestUrl);

        if(!searchBodyCache.exists(requestUrl)) {
            synchronized (searchCacheLock) {
                if (!searchBodyCache.exists(requestUrl)) {
                    log.info("searchBody缓存失效, 正在更新...");
                    JsonObject jsonObject;
                    HttpGet httpGetRequest = pixivDownload.createHttpGetRequest(requestUrl);
                    HttpResponse response = pixivDownload.getHttpClient().execute(httpGetRequest);

                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    log.info("ResponseBody: {}", responseBody);
                    jsonObject = gson.fromJson(responseBody, JsonObject.class);

                    if (jsonObject.get("error").getAsBoolean()) {
                        log.error("接口请求错误, 错误信息: {}", jsonObject.get("message").getAsString());
                        return "处理命令时发生错误！";
                    }

                    Date newExpireDate = new Date();
                    long expire = 7200 * 1000;
                    String propValue = CQPluginMain.globalProp.getProperty("cache.searchBody.expire", "7200000");
                    try {
                        expire = Long.parseLong(propValue);
                    } catch (Exception e) {
                        log.warn("全局配置项 \"{}\" 值非法, 已使用默认值: {}", propValue, expire);
                    }

                    newExpireDate.setTime(newExpireDate.getTime() + expire);
                    searchBodyCache.update(requestUrl, jsonObject, newExpireDate);
                    log.info("searchBody缓存已更新(到期时间: {})", newExpireDate);
                }
            }
        } else {
            log.info("搜索缓存命中.");
        }

        JsonObject resultBody = searchBodyCache.getCache(requestUrl).getAsJsonObject("body");

        StringBuilder result = new StringBuilder("内容 " + content + " 的搜索结果：\n");
        log.info("正在处理信息...");
        int limit = 8;
        try {
            limit = Integer.parseInt(CQPluginMain.globalProp.getProperty("search.ItemCountLimit", "8"));
        } catch (Exception e) {
            log.warn("参数转换异常!将使用默认值(" + limit + ")", e);
        }
        for (PixivSearchBuilder.SearchArea searchArea : PixivSearchBuilder.SearchArea.values()) {
            if (!resultBody.has(searchArea.jsonKey) || resultBody.getAsJsonObject(searchArea.jsonKey).getAsJsonArray("data").size() == 0) {
                log.debug("返回数据不包含 {}", searchArea.jsonKey);
                continue;
            }
            JsonArray illustsArray = resultBody
                    .getAsJsonObject(searchArea.jsonKey).getAsJsonArray("data");
            ArrayList<JsonElement> illustsList = new ArrayList<>();
            illustsArray.forEach(illustsList::add);
            illustsList.sort((o1, o2) -> {
                try {
                    int illustLikeCount1 = getIllustPreLoadData(o1.getAsJsonObject().get("illustId").getAsInt()).get("likeCount").getAsInt();
                    int illustLikeCount2 = getIllustPreLoadData(o2.getAsJsonObject().get("illustId").getAsInt()).get("likeCount").getAsInt();
                    return Integer.compare(illustLikeCount2, illustLikeCount1);
                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            });

            log.info("已找到与 {} 相关插图信息({})：", content, searchArea.name().toLowerCase());
            int count = 1;
            for (JsonElement jsonElement : illustsList) {
                if (count > limit) {
                    break;
                }
                JsonObject illustObj = jsonElement.getAsJsonObject();
                if (!illustObj.has("illustId")) {
                    continue;
                }
                int illustId = illustObj.get("illustId").getAsInt();
                StringBuilder builder = new StringBuilder("[");
                illustObj.get("tags").getAsJsonArray().forEach(el -> builder.append(el.getAsString()).append(", "));
                builder.replace(builder.length() - 2, builder.length(), "]");
                log.debug("{} ({} / {})\n\t作品id: {}, \n\t作者名(作者id): {} ({}), \n\t作品标题: {}, \n\t作品Tags: {}, \n\t作品链接: {}",
                        searchArea.name(),
                        count,
                        illustsList.size(),
                        illustId,
                        illustObj.get("userName").getAsString(),
                        illustObj.get("userId").getAsInt(),
                        illustObj.get("illustTitle").getAsString(),
                        builder,
                        PixivURL.getPixivRefererLink(illustId)
                );

                String imageMsg = getImageById(illustId, PixivDownload.PageQuality.REGULAR, 1);
                if (isNoSafe(illustId, CQPluginMain.globalProp, true)) {
                    log.warn("作品Id {} 为R-18作品, 跳过.", illustId);
                    count--;
                    continue;
                }

                result.append(searchArea.name()).append(" (").append(count).append(" / ").append(illustsList.size()).append(")\n\t作品id: ").append(illustId)
                        .append(", \n\t作者名: ").append(illustObj.get("userName").getAsString())
                        .append("\n\t作品标题: ").append(illustObj.get("illustTitle").getAsString()).append("\n").append(imageMsg).append("\n");
                count++;
            }
            if (count > limit) {
                break;
            }
        }
        return Strings.nullToEmpty(result.toString()) + "预览图片并非原图，使用“.cgj image -id 作品id”获取原图";
    }

    @Command(commandName = "pages")
    public static String getPagesList(@Argument(name = "id") int illustId, @Argument(name = "quality", force = false) PixivDownload.PageQuality quality) {
        try {
            List<String> pagesList = PixivDownload.getIllustAllPageDownload(pixivDownload.getHttpClient(), pixivDownload.getCookieStore(), illustId, quality);
            StringBuilder builder = new StringBuilder("作品ID ").append(illustId).append(" 共有").append(pagesList.size()).append("页：").append("\n");
            int index = 0;
            for (String link : pagesList) {
                builder.append("Page ").append(++index).append(": ").append(link).append("\n");
            }
            return builder.toString();
        } catch (IOException e) {
            log.error("获取作品所有页面下载链接失败！", e);
            return "发生错误，无法完成命令";
        }
    }

    @Command(commandName = "artworks")
    public static String artworksLink(@Argument(name = "id") int illustId) {
        try {
            if (isNoSafe(illustId, CQPluginMain.globalProp, false)) {
                log.warn("作品Id {} 已被屏蔽.", illustId);
                return "由于相关设置，该作品已被屏蔽！";
            }
        } catch (IOException e) {
            log.error("获取作品信息失败!", e);
            return "作品信息无法获取！";
        }
        return PixivURL.getPixivRefererLink(illustId);
    }

    @Command(commandName = "image")
    public static String getImageById(@Argument(name = "id") int illustId,
                                                   @Argument(name = "quality", force = false) PixivDownload.PageQuality quality,
                                                   @Argument(name = "page", force = false, defaultValue = "1") int pageIndex) {
        log.info("IllustId: {}, Quality: {}, PageIndex: {}", illustId, quality.name(), pageIndex);
        List<String> pagesList;
        try {
            pagesList = getIllustPages(illustId, quality);
        } catch (IOException e) {
            log.error("获取下载链接列表时发生异常", e);
            return "发生网络异常，无法获取图片！";
        }
        if (pagesList.size() < pageIndex || pageIndex <= 0) {
            log.warn("指定的页数超出了总页数({} / {})", pageIndex, pagesList.size());
            return "指定的页数超出了范围(总共 " + pagesList.size() + " 页)";
        }

        try {
            if (isNoSafe(illustId, CQPluginMain.globalProp, false)) {
                log.warn("作品 {} 存在R-18内容且设置\"image.allowR18\"为false，将屏蔽该作品不发送.", illustId);
                pageIndex = -1;
            }
        } catch (IOException e) {
            log.warn("作品信息无法获取!", e);
            return "发生网络异常，无法获取图片！";
        }

        int index = 0;
        String targetLink = null;
        File targetFile;
        File currentImageFile;
        for (String link : pagesList) {
            index++;
            if (index == pageIndex) {
                targetLink = link;
            }
            currentImageFile = new File(getImageStoreDir(), link.substring(link.lastIndexOf("/") + 1));
            if (!imageCache.containsKey(link)) {
                HttpHead headRequest = new HttpHead(link);
                headRequest.addHeader("Referer", PixivURL.getPixivRefererLink(illustId));
                HttpResponse headResponse;
                try {
                    headResponse = pixivDownload.getHttpClient().execute(headRequest);
                } catch (IOException e) {
                    log.error("获取图片大小失败！", e);
                    return "图片获取失败!";
                }
                String contentLengthStr = headResponse.getFirstHeader(HttpHeaderNames.CONTENT_LENGTH.toString()).getValue();
                if (currentImageFile.exists() && currentImageFile.length() == Long.parseLong(contentLengthStr)) {
                    imageCache.put(link, currentImageFile);
                    log.info("作品Id {} 第 {} 页缓存已补充.", illustId, index);
                    continue;
                }

                if (index == pageIndex) {
                    try {
                        imageCacheExecutor.executorSync(new ImageCacheObject(imageCache, illustId, link, currentImageFile));
                    } catch (InterruptedException e) {
                        log.warn("图片下载遭到中断!", e);
                    }
                } else {
                    imageCacheExecutor.executor(
                            new ImageCacheObject(imageCache, illustId, link, currentImageFile));
                }
            }
        }

        if (pageIndex == -1) {
            return "（根据设置，该作品已被屏蔽！）";
        }

        if (targetLink == null) {
            return "未找到对应的图片！";
        }

        targetFile = imageCache.get(targetLink);
        return targetFile == null ? "未找到对应的图片！" : CQCode.image(getImageStoreDir().getName() + "/" + targetFile.getName());
    }

    static void clearCache() {
        log.warn("正在清除所有缓存...");
        imageCache.clear();
        illustInfoCache.clear();
        illustPreLoadDataCache.clear();
        pagesCache.clear();
        searchBodyCache.clear();
        File imageStoreDir = new File(System.getProperty("cgj.cqRootDir") + "data/image/cgj/");
        File[] listFiles = imageStoreDir.listFiles();
        if (listFiles == null) {
            log.warn("图片缓存目录为空或内部文件获取失败!");
        } else {
            for (File file : listFiles) {
                log.debug("图片文件 {} 删除: {}", file.getName(), file.delete());
            }
        }
        log.info("图片缓存目录删除: {}", imageStoreDir.delete());
        log.warn("缓存删除完成.");
    }

    /*
    下一目标：
    添加定时发图
    定时发图支持设置关注标签
    标签....标签支持搜索吧
     */

    private static boolean isNoSafe(int illustId, Properties settingProp, boolean returnRaw) throws IOException {
        boolean rawValue = getIllustInfo(illustId).getAsJsonArray("tags").contains(new JsonPrimitive("R-18"));
        return returnRaw || settingProp == null ? rawValue : rawValue && !settingProp.getProperty("image.allowR18", "false").equalsIgnoreCase("true");
    }

    private final static Object illustInfoLock = new Object();
    private static JsonObject getIllustInfo(int illustId) throws IOException {
        String illustIdStr = Integer.toString(illustId);
        if (!illustInfoCache.exists(illustIdStr)) {
            synchronized (illustInfoLock) {
                if (!illustInfoCache.exists(illustIdStr)) {
                    File cacheFile = new File(getImageStoreDir(), illustId + ".illustInfo.json");
                    log.info("IllustInfoFileName: {}", cacheFile.getName());
                    JsonObject illustInfoObj;
                    if (!cacheFile.exists()) {
                        try {
                            cacheFile.createNewFile();
                            illustInfoObj = pixivDownload.getIllustInfoByIllustId(illustId);
                            Files.write(cacheFile.toPath(), gson.toJson(illustInfoObj).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                        } catch (IOException e) {
                            cacheFile.delete();
                            throw e;
                        }
                    } else {
                        illustInfoObj = gson.fromJson(new FileReader(cacheFile), JsonObject.class);
                    }
                    illustInfoCache.update(illustIdStr, illustInfoObj, null);
                }
            }
        }
        return illustInfoCache.getCache(illustIdStr);
    }

    private final static Object illustPreLoadDataLock = new Object();
    public static JsonObject getIllustPreLoadData(int illustId) throws IOException {
        String illustIdStr = Integer.toString(illustId);
        if (!illustPreLoadDataCache.exists(illustIdStr)) {
            synchronized (illustPreLoadDataLock) {
                if (!illustPreLoadDataCache.exists(illustIdStr)) {
                    File cacheFile = new File(getImageStoreDir(), illustId + ".illustPreLoadData.json");
                    log.info("缓存失效, 正在更新...");
                    log.info("illustPreLoadDataFileName: {}", cacheFile.getName());
                    JsonObject preLoadDataObj;
                    if (!cacheFile.exists()) {
                        try {
                            cacheFile.createNewFile();
                            preLoadDataObj = pixivDownload.getIllustPreLoadDataById(illustId)
                                    .getAsJsonObject("illust")
                                    .getAsJsonObject(Integer.toString(illustId));
                            Files.write(cacheFile.toPath(), gson.toJson(preLoadDataObj).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                        } catch(IOException e) {
                            cacheFile.delete();
                            throw e;
                        }
                    } else {
                        preLoadDataObj = gson.fromJson(new FileReader(cacheFile), JsonObject.class);
                    }

                    long expire = 7200 * 1000;
                    String propValue = CQPluginMain.globalProp.getProperty("cache.illustPreLoadData.expire", "7200000");
                    log.info("PreLoadData有效时间设定: {}", propValue);
                    try {
                        expire = Long.parseLong(propValue);
                    } catch (Exception e) {
                        log.warn("全局配置项 \"{}\" 值非法, 已使用默认值: {}", propValue, expire);
                    }

                    Date newExpire = new Date();
                    newExpire.setTime(newExpire.getTime() + expire);
                    illustPreLoadDataCache.update(illustIdStr, preLoadDataObj, newExpire);
                    log.info("作品Id {} preLoadData缓存已更新(到期时间: {})", illustId, newExpire);
                }
            }
        }
            return illustPreLoadDataCache.getCache(illustIdStr);
    }

    private final static Object illustPagesLock = new Object();
    public static List<String> getIllustPages(int illustId, PixivDownload.PageQuality quality) throws IOException {
        String pagesSign = illustId + "." + quality.name();
        if (!pagesCache.exists(pagesSign)) {
            synchronized (illustPagesLock) {
                if (!pagesCache.exists(pagesSign)) {
                    File cacheFile = new File(getImageStoreDir(), illustId + "." + quality.name() + ".illustPages.json");
                    log.info("illustPagesFileName: {}", cacheFile.getName());
                    List<String> linkList;
                    if (!cacheFile.exists()) {
                        try {
                            cacheFile.createNewFile();
                            linkList = PixivDownload.getIllustAllPageDownload(pixivDownload.getHttpClient(), pixivDownload.getCookieStore(), illustId, quality);
                            JsonArray jsonArray = new JsonArray(linkList.size());
                            linkList.forEach(jsonArray::add);
                            Files.write(cacheFile.toPath(), gson.toJson(jsonArray).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                        } catch (IOException e) {
                            cacheFile.delete();
                            throw e;
                        }
                    } else {
                        JsonArray jsonArray = gson.fromJson(new FileReader(cacheFile), JsonArray.class);
                        linkList = new ArrayList<>(jsonArray.size());
                        jsonArray.forEach(jsonElement -> linkList.add(jsonElement.getAsString()));
                    }
                    pagesCache.update(pagesSign, linkList, null);
                }
            }
        }

        return pagesCache.getCache(pagesSign);
    }
    
    private static File getImageStoreDir() {
        if(!imageStoreDir.exists() && !imageStoreDir.mkdirs()) {
            log.warn("酷Q图片缓存目录失效！(Path: {} )", imageStoreDir.getAbsolutePath());
            throw new RuntimeException(new IOException("文件夹创建失败!"));
        }
        return imageStoreDir;
    }

    private final static Object rankingLock = new Object();
    private static List<JsonObject> getRankingInfoByCache(PixivURL.RankingContentType contentType, PixivURL.RankingMode mode, Date queryDate, int start, int range) throws IOException {
        String date = new SimpleDateFormat("yyyyMMdd").format(queryDate);
        //int requestSign = ("Ranking." + contentType.name() + "." + mode.name() + "." + date).hashCode();
        String requestSign = "Ranking." + contentType.name() + "." + mode.name() + "." + date;
        if(!rankingCache.exists(requestSign)) {
            synchronized(rankingLock) {
                if(!rankingCache.exists(requestSign)) {
                    log.info("Ranking缓存失效, 正在更新...(RequestSign: {})", requestSign);
                    File cacheFile = new File(getImageStoreDir(), date + "." + contentType.name() + "." + mode.modeParam + ".ranking.json");
                    JsonArray rankingArr;
                    if(!cacheFile.exists()) {
                        List<JsonObject> rankingResult = pixivDownload.getRanking(contentType, mode, queryDate, 1, 500);
                        rankingArr = new JsonArray(rankingResult.size());
                        rankingResult.forEach(rankingArr::add);
                        JsonObject cacheBody = new JsonObject();
                        cacheBody.addProperty("updateTimestamp", new Date().getTime());
                        cacheBody.addProperty("ContentType", contentType.name());
                        cacheBody.addProperty("RankingMode", mode.modeParam);
                        cacheBody.add("ranking", rankingArr);
                        Files.write(cacheFile.toPath(), gson.toJson(cacheBody).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                        log.info("已从Pixiv获取数据并缓存到文件.");
                    } else {
                        JsonObject cacheBody = gson.fromJson(new FileReader(cacheFile), JsonObject.class);
                        rankingArr = cacheBody.getAsJsonArray("ranking");
                        log.info("已从文件获取缓存数据.");
                    }

                    rankingCache.update(requestSign, rankingArr, null);
                }
            }
        }

        return PixivDownload.getRanking(rankingCache.getCache(requestSign), start, range);
    }

}
