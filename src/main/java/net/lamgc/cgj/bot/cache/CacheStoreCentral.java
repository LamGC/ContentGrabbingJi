package net.lamgc.cgj.bot.cache;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lamgc.cgj.bot.BotCode;
import net.lamgc.cgj.bot.BotCommandProcess;
import net.lamgc.cgj.bot.SettingProperties;
import net.lamgc.cgj.bot.boot.BotGlobal;
import net.lamgc.cgj.exception.HttpRequestException;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivSearchLinkBuilder;
import net.lamgc.cgj.pixiv.PixivURL;
import net.lamgc.cgj.util.Locker;
import net.lamgc.cgj.util.LockerMap;
import net.lamgc.cgj.util.PixivUtils;
import net.lamgc.cgj.util.URLs;
import net.lamgc.utils.encrypt.MessageDigestUtils;
import net.lz1998.cq.utils.CQCode;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public final class CacheStoreCentral {

    private final static Logger log = LoggerFactory.getLogger(CacheStoreCentral.class);

    private static CacheStoreCentral central = new CacheStoreCentral();

    public static CacheStoreCentral getCentral() {
        if(central == null) {
            initialCentral();
        }
        return central;
    }

    private synchronized static void initialCentral() {
        if(central != null) {
            return;
        }

        central = new CacheStoreCentral();
    }

    private final LockerMap<String> lockerMap = new LockerMap<>();

    private CacheStoreCentral() {}

    private final Hashtable<String, File> imageCache = new Hashtable<>();

    private final CacheStore<JsonElement> imageChecksumCache =
            new JsonRedisCacheStore(BotGlobal.getGlobal().getRedisServer(),
                    "imageChecksum", BotGlobal.getGlobal().getGson());

    /*
     * 注意：
     *      在启用了远端缓存的情况下, 不允许滥用本地缓存
     *      只有在处理命令中需要短时间大量存取的缓存项才能进行本地缓存(例如PreLoadData需要在排序中大量获取);
     *      如果没有短时间大量存取的需要, 切勿使用本地缓存
     */

    /**
     * 作品信息缓存 - 不过期
     */
    private final CacheStore<JsonElement> illustInfoCache =
            new JsonRedisCacheStore(BotGlobal.getGlobal().getRedisServer(),
                    "illustInfo", BotGlobal.getGlobal().getGson());

    /**
     * 作品信息预加载数据 - 有效期 2 小时, 本地缓存有效期 0.5 ± 0.25 小时
     */
    private final CacheStore<JsonElement> illustPreLoadDataCache =
            CacheStoreUtils.hashLocalHotDataStore(
                    new JsonRedisCacheStore(BotGlobal.getGlobal().getRedisServer(),
                            "illustPreLoadData", BotGlobal.getGlobal().getGson()), 600000, 120000);
    /**
     * 搜索内容缓存, 有效期 2 小时
     */
    private final CacheStore<JsonElement> searchBodyCache =
            new JsonRedisCacheStore(BotGlobal.getGlobal().getRedisServer(),
                    "searchBody", BotGlobal.getGlobal().getGson());

    /**
     * 排行榜缓存, 不过期
     */
    private final CacheStore<List<JsonObject>> rankingCache =
            new JsonObjectRedisListCacheStore(BotGlobal.getGlobal().getRedisServer(),
                    "ranking", BotGlobal.getGlobal().getGson());

    /**
     * 作品页面下载链接缓存 - 不过期
     */
    private final CacheStore<List<String>> pagesCache =
            new StringListRedisCacheStore(BotGlobal.getGlobal().getRedisServer(), "imagePages");

    /**
     * 清空所有缓存
     */
    public void clearCache() {
        imageCache.clear();
        illustInfoCache.clear();
        illustPreLoadDataCache.clear();
        searchBodyCache.clear();
        rankingCache.clear();
        pagesCache.clear();
    }

    /**
     * 通过illustId获取作品图片
     * @param fromGroup 来源群(系统提供)
     * @param illustId 作品Id
     * @param quality 图片质量
     * @param pageIndex 指定页面索引, 从1开始
     * @return 如果成功, 返回BotCode, 否则返回错误信息.
     */
    public String getImageById(long fromGroup, int illustId, PixivDownload.PageQuality quality, int pageIndex) throws InterruptedException {
        log.debug("IllustId: {}, Quality: {}, PageIndex: {}", illustId, quality.name(), pageIndex);
        if(pageIndex <= 0) {
            log.warn("指定的页数不能小于或等于0: {}", pageIndex);
            return "指定的页数不能小于或等于0！";
        }

        try {
            if (BotCommandProcess.isNoSafe(illustId, SettingProperties.getProperties(fromGroup), false)) {
                log.warn("作品 {} 存在R-18内容且设置\"image.allowR18\"为false，将屏蔽该作品不发送.", illustId);
                return "（根据设置，该作品已被屏蔽！）";
            } else if(BotCommandProcess.isReported(illustId)) {
                log.warn("作品Id {} 被报告, 正在等待审核, 跳过该作品.", illustId);
                return "（该作品已被封印）";
            }
        } catch (IOException e) {
            log.warn("作品信息无法获取!", e);
            return "（发生网络异常，无法获取图片！）";
        }

        List<String> pagesList;
        try {
            pagesList = getIllustPages(illustId, quality, false);
        } catch (IOException e) {
            log.error("获取下载链接列表时发生异常", e);
            return "发生网络异常，无法获取图片！";
        }

        if(log.isDebugEnabled()) {
            StringBuilder logBuilder = new StringBuilder("作品Id " + illustId + " 所有页面下载链接: \n");
            AtomicInteger index = new AtomicInteger();
            pagesList.forEach(item ->
                    logBuilder.append(index.incrementAndGet()).append(". ").append(item).append("\n"));
            log.debug(logBuilder.toString());
        }

        if (pagesList.size() < pageIndex) {
            log.warn("指定的页数超出了总页数({} / {})", pageIndex, pagesList.size());
            return "指定的页数超出了范围(总共 " + pagesList.size() + " 页)";
        }

        String downloadLink = pagesList.get(pageIndex - 1);
        String fileName = URLs.getResourceName(Strings.nullToEmpty(downloadLink));
        File imageFile = new File(BotGlobal.getGlobal().getImageStoreDir(),
                downloadLink.substring(downloadLink.lastIndexOf("/") + 1));
        log.debug("FileName: {}, DownloadLink: {}", fileName, downloadLink);
        if(!imageCache.containsKey(fileName)) {
            if(imageFile.exists() && imageFile.isFile()) {
                ImageChecksum imageChecksum = getImageChecksum(illustId, pageIndex);
                if(imageChecksum != null) {
                    try {
                        log.trace("正在检查作品Id {} 第 {} 页图片文件 {} ...", illustId, pageIndex, imageFile.getName());
                        if (ImageChecksum.checkFile(imageChecksum, Files.readAllBytes(imageFile.toPath()))) {
                            imageCache.put(URLs.getResourceName(downloadLink), imageFile);
                            log.trace("作品Id {} 第 {} 页缓存已补充.", illustId, pageIndex);
                            return getImageToBotCode(imageFile, false).toString();
                        } else {
                            log.warn("图片文件 {} 校验失败, 重新下载图片...", imageFile.getName());
                        }
                    } catch(IOException e) {
                        log.error("文件检验时读取失败, 重新下载文件...(file: {})", imageFile.getPath());
                    }
                } else {
                    log.warn("图片存在但校验不存在, 重新下载图片...");
                }
            }

            try {
                Throwable throwable = ImageCacheStore.executeCacheRequest(
                        new ImageCacheObject(imageCache, illustId, pageIndex, downloadLink, imageFile));
                if(throwable != null) {
                    throw throwable;
                }
            } catch (InterruptedException e) {
                log.warn("图片缓存被中断", e);
                throw e;
            } catch (Throwable e) {
                log.error("图片 {} 获取失败:\n{}", illustId + "p" + pageIndex, Throwables.getStackTraceAsString(e));
                return "(错误: 图片获取出错)";
            }
        } else {
            log.trace("图片 {} 缓存命中.", fileName);
        }

        return getImageToBotCode(imageCache.get(fileName), false).toString();
    }

    /**
     * 通过文件获取图片的BotCode代码
     * @param targetFile 图片文件
     * @param updateCache 是否刷新缓存(只是让机器人重新上传, 如果上传接口有重复检测的话是无法处理的)
     * @return 返回设定好参数的BotCode
     */
    @SuppressWarnings("SameParameterValue")
    private BotCode getImageToBotCode(File targetFile, boolean updateCache) {
        String fileName = Objects.requireNonNull(targetFile, "targetFile is null").getName();
        BotCode code = BotCode.parse(
                CQCode.image(BotGlobal.getGlobal().getImageStoreDir().getName() + "/" + fileName));
        code.addParameter("absolutePath", targetFile.getAbsolutePath());
        code.addParameter("imageName", fileName.substring(0, fileName.lastIndexOf(".")));
        code.addParameter("updateCache", updateCache ? "true" : "false");
        return code;
    }

    /**
     * 获取作品信息
     * @param illustId 作品Id
     * @param flushCache 强制刷新缓存
     * @return 返回作品信息
     * @throws IOException 当Http请求发生异常时抛出
     * @throws NoSuchElementException 当作品未找到时抛出
     */
    public JsonObject getIllustInfo(int illustId, boolean flushCache)
            throws IOException, NoSuchElementException {
        Locker<String> locker = buildSyncKey(Integer.toString(illustId));
        String illustIdStr = locker.getKey();
        JsonObject illustInfoObj = null;
        if (!illustInfoCache.exists(illustIdStr) || flushCache) {
            try {
                locker.lock();
                synchronized (locker) {
                    if (!illustInfoCache.exists(illustIdStr) || flushCache) {
                        illustInfoObj = BotGlobal.getGlobal().getPixivDownload().getIllustInfoByIllustId(illustId);
                        illustInfoCache.update(illustIdStr, illustInfoObj, null);
                    }
                }
            } finally {
                locker.unlock();
            }
        }

        if(Objects.isNull(illustInfoObj)) {
            illustInfoObj = illustInfoCache.getCache(illustIdStr).getAsJsonObject();
            log.trace("作品Id {} IllustInfo缓存命中.", illustId);
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
    public JsonObject getIllustPreLoadData(int illustId, boolean flushCache) throws IOException {
        Locker<String> locker = buildSyncKey(Integer.toString(illustId));
        String illustIdStr = locker.getKey();
        JsonObject result = null;
        if (!illustPreLoadDataCache.exists(illustIdStr) || flushCache) {
            try {
                locker.lock();
                synchronized (locker) {
                    if (!illustPreLoadDataCache.exists(illustIdStr) || flushCache) {
                        log.trace("IllustId {} 缓存失效, 正在更新...", illustId);
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
                        log.trace("作品Id {} preLoadData缓存已更新(有效时间: {})", illustId, expire);
                    }
                }
            } finally {
                locker.unlock();
            }
        }

        if(Objects.isNull(result)) {
            result = illustPreLoadDataCache.getCache(illustIdStr).getAsJsonObject();
            log.trace("作品Id {} PreLoadData缓存命中.", illustId);
        }
        return result;
    }

    public List<String> getIllustPages(int illustId, PixivDownload.PageQuality quality, boolean flushCache)
            throws IOException {
        Locker<String> locker
                = buildSyncKey(Integer.toString(illustId), ".", quality.name());
        String pagesSign = locker.getKey();
        List<String> result = null;
        if (!pagesCache.exists(pagesSign) || flushCache) {
            try {
                locker.lock();
                synchronized (locker) {
                    if (!pagesCache.exists(pagesSign) || flushCache) {
                        List<String> linkList = PixivDownload
                                .getIllustAllPageDownload(BotGlobal.getGlobal().getPixivDownload().getHttpClient(),
                                        BotGlobal.getGlobal().getPixivDownload().getCookieStore(), illustId, quality);
                        result = linkList;
                        pagesCache.update(pagesSign, linkList, null);
                    }
                }
            } finally {
                locker.unlock();
            }
        }

        if(Objects.isNull(result)) {
            result = pagesCache.getCache(pagesSign);
            log.trace("作品Id {} Pages缓存命中.", illustId);
        }
        return result;
    }

    private final Random expireTimeFloatRandom = new Random();
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
    public List<JsonObject> getRankingInfoByCache(PixivURL.RankingContentType contentType,
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
        Locker<String> locker
                = buildSyncKey(contentType.name(), ".", mode.name(), ".", date);
        String requestSign = locker.getKey();
        List<JsonObject> result = null;
        if(!rankingCache.exists(requestSign) || flushCache) {
            try {
                locker.lock();
                synchronized (locker) {
                    if (!rankingCache.exists(requestSign) || flushCache) {
                        log.trace("Ranking缓存失效, 正在更新...(RequestSign: {})", requestSign);
                        List<JsonObject> rankingResult = BotGlobal.getGlobal().getPixivDownload()
                                .getRanking(contentType, mode, queryDate, 1, 500);
                        long expireTime = 0;
                        if (rankingResult.size() == 0) {
                            expireTime = 5400000 + expireTimeFloatRandom.nextInt(1800000);
                            log.warn("数据获取失败, 将设置浮动有效时间以准备下次更新. (ExpireTime: {}ms)", expireTime);
                        }
                        result = new ArrayList<>(rankingResult).subList(start - 1, start + range - 1);
                        rankingCache.update(requestSign, rankingResult, expireTime);
                        log.trace("Ranking缓存更新完成.(RequestSign: {})", requestSign);
                    }
                }
            } finally {
                locker.unlock();
            }
        }

        if (Objects.isNull(result)) {
            result = rankingCache.getCache(requestSign, start - 1, range);
            log.trace("RequestSign [{}] 缓存命中.", requestSign);
        }
        return result;
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
    public JsonObject getSearchBody(
            String content,
            String type,
            String area,
            String includeKeywords,
            String excludeKeywords,
            String contentOption,
            int pageIndex
    ) throws IOException {
        return getSearchBody(PixivUtils.buildSearchLinkBuilderFromString(content, type, area,
                includeKeywords, excludeKeywords, contentOption, pageIndex));
    }

    /**
     * 获取搜索结果
     * @param searchBuilder 需要执行搜索的搜索链接构造器
     * @return 返回完整搜索结果
     * @throws IOException 当请求发生异常, 或接口返回异常信息时抛出.
     */
    public JsonObject getSearchBody(PixivSearchLinkBuilder searchBuilder) throws IOException {
        log.debug("正在搜索作品, 条件: {}", searchBuilder.getSearchCondition());
        String requestUrl = searchBuilder.buildURL();
        String searchIdentify = requestUrl.substring(requestUrl.lastIndexOf("/", requestUrl.lastIndexOf("/") - 1) + 1);
        Locker<String> locker
                = buildSyncKey(searchIdentify);
        log.debug("RequestUrl: {}", requestUrl);
        JsonObject resultBody = null;
        if(!searchBodyCache.exists(searchIdentify)) {
            try {
                locker.lock();
                synchronized (locker) {
                    if (!searchBodyCache.exists(searchIdentify)) {
                        log.trace("searchBody缓存失效, 正在更新...");
                        JsonObject jsonObject;
                        HttpGet httpGetRequest = BotGlobal.getGlobal().getPixivDownload().
                                createHttpGetRequest(requestUrl);
                        HttpResponse response = BotGlobal.getGlobal().getPixivDownload().
                                getHttpClient().execute(httpGetRequest);

                        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                        log.trace("ResponseBody: {}", responseBody);
                        jsonObject = BotGlobal.getGlobal().getGson().fromJson(responseBody, JsonObject.class);

                        if (jsonObject.get("error").getAsBoolean()) {
                            log.error("接口请求错误, 错误信息: {}", jsonObject.get("message").getAsString());
                            throw new HttpRequestException(response.getStatusLine(), responseBody);
                        }

                        long expire = 7200 * 1000;
                        String propValue = SettingProperties
                                .getProperty(SettingProperties.GLOBAL, "cache.searchBody.expire", "7200000");
                        try {
                            expire = Long.parseLong(propValue);
                        } catch (Exception e) {
                            log.warn("全局配置项 \"{}\" 值非法, 已使用默认值: {}", propValue, expire);
                        }
                        resultBody = jsonObject;
                        searchBodyCache.update(searchIdentify, jsonObject, expire);
                        log.trace("searchBody缓存已更新(有效时间: {})", expire);
                    } else {
                        log.trace("搜索缓存命中.");
                    }
                }
            } finally {
                locker.unlock();
            }
        } else {
            log.trace("搜索缓存命中.");
        }

        if(Objects.isNull(resultBody)) {
            resultBody = searchBodyCache.getCache(searchIdentify).getAsJsonObject();
        }
        return resultBody.getAsJsonObject("body");
    }

    protected ImageChecksum getImageChecksum(int illustId, int pageIndex) {
        String cacheKey = illustId + ":" + pageIndex;
        if(!imageChecksumCache.exists(cacheKey)) {
            return null;
        } else {
            return ImageChecksum.fromJsonObject(imageChecksumCache.getCache(cacheKey).getAsJsonObject());
        }
    }

    protected void setImageChecksum(ImageChecksum checksum) {
        String cacheKey = checksum.getIllustId() + ":" + checksum.getPage();
        imageChecksumCache.update(cacheKey, ImageChecksum.toJsonObject(checksum), 0);
    }

    /**
     * 合并String并存取到常量池, 以保证对象一致
     * @param keys String对象
     * @return 合并后, 如果常量池存在合并后的结果, 则返回常量池中的对象, 否则存入常量池后返回.
     */
    private Locker<String> buildSyncKey(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String string : keys) {
            sb.append(string);
        }
        return lockerMap.createLocker(sb.toString(), true);
    }

    /**
     * 图片检验信息
     */
    public static class ImageChecksum implements Serializable {
        
        private final static MessageDigestUtils.Algorithm ALGORITHM = MessageDigestUtils.Algorithm.SHA256;

        private ImageChecksum() {}
        
        private int illustId;

        private int page;

        private String fileName;

        private long size;

        private byte[] checksum;

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public byte[] getChecksum() {
            return checksum;
        }

        public void setChecksum(byte[] checksum) {
            this.checksum = checksum;
        }

        public int getIllustId() {
            return illustId;
        }

        public void setIllustId(int illustId) {
            this.illustId = illustId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public static ImageChecksum buildImageChecksumFromStream(
                int illustId, int pageIndex,
                String fileName, InputStream imageStream) throws IOException {
            ImageChecksum checksum = new ImageChecksum();
            checksum.setIllustId(illustId);
            checksum.setPage(pageIndex);
            checksum.setFileName(fileName);
            ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
            checksum.setSize(IOUtils.copyLarge(imageStream, bufferStream));
            checksum.setChecksum(
                    MessageDigestUtils.encrypt(bufferStream.toByteArray(), ALGORITHM));
            return checksum;
        }

        /**
         * 将图片检验信息转换成JsonObject
         * @param checksum 检验信息对象
         * @return 转换后的JsonObject对象
         */
        public static JsonObject toJsonObject(ImageChecksum checksum) {
            JsonObject result = new JsonObject();
            result.addProperty("illustId", checksum.getIllustId());
            result.addProperty("page", checksum.getPage());
            result.addProperty("fileName", checksum.getFileName());
            result.addProperty("size", checksum.getSize());
            result.addProperty("checksum", Base64.getEncoder().encodeToString(checksum.getChecksum()));
            return result;
        }

        /**
         * 从JsonObject转换到图片检验信息
         * @param checksumObject JsonObject对象
         * @return 转换后的图片检验信息对象
         */
        public static ImageChecksum fromJsonObject(JsonObject checksumObject) {
            ImageChecksum checksum = new ImageChecksum();
            checksum.setIllustId(checksumObject.get("illustId").getAsInt());
            checksum.setPage(checksumObject.get("page").getAsInt());
            checksum.setFileName(checksumObject.get("fileName").getAsString());
            checksum.setSize(checksumObject.get("size").getAsLong());
            checksum.setChecksum(Base64.getDecoder().decode(checksumObject.get("checksum").getAsString()));
            return checksum;
        }

        /**
         * 比对图片文件是否完整.
         * @param checksum 图片检验信息
         * @param imageData 图片数据
         * @return 如果检验成功, 则返回true
         */
        public static boolean checkFile(ImageChecksum checksum, byte[] imageData) {
            byte[] sha256Checksum = MessageDigestUtils.encrypt(imageData, ALGORITHM);
            return checksum.getSize() == imageData.length &&
                   Arrays.equals(checksum.getChecksum(), sha256Checksum);
        }

        @Override
        public String toString() {
            return "ImageChecksum{" +
                    "illustId=" + illustId +
                    ", page=" + page +
                    ", fileName='" + fileName + '\'' +
                    ", size=" + size +
                    ", checksum=" + Base64.getEncoder().encodeToString(getChecksum()) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageChecksum checksum1 = (ImageChecksum) o;
            return illustId == checksum1.illustId &&
                    page == checksum1.page &&
                    size == checksum1.size &&
                    Objects.equals(fileName, checksum1.fileName) &&
                    Arrays.equals(checksum, checksum1.checksum);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(illustId, page, fileName, size);
            result = 31 * result + Arrays.hashCode(checksum);
            return result;
        }
    }
}
