package net.lamgc.cgj.pixiv;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

@SuppressWarnings("ALL")
public class PixivDownload {

    private final static Logger log = LoggerFactory.getLogger(PixivDownload.class);

    private final HttpClient httpClient;

    private final CookieStore cookieStore;

    /**
     * 构造一个PixivDownload对象
     * @param cookieStore 存在已登录Pixiv的CookieStore对象
     */
    public PixivDownload(CookieStore cookieStore) {
        this(cookieStore, null);
    }

    /**
     * 构造一个PixivDownload对象
     * @param cookieStore 存在已登录Pixiv的CookieStore对象
     * @param proxy 访问代理
     */
    public PixivDownload(CookieStore cookieStore, HttpHost proxy) {
        this.cookieStore = cookieStore;
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultCookieStore(cookieStore);
        // UA: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36
        ArrayList<Header> defaultHeaders = new ArrayList<>(2);
        defaultHeaders.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36"));
        builder.setDefaultHeaders(defaultHeaders);
        builder.setProxy(proxy);
        httpClient = builder.build();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * 获取帐号所有的收藏插画，并以输入流形式提供
     * @param fn 回调函数，函数传进的InputStream无需手动关闭
     * @throws IOException 当获取时发生异常则直接抛出
     */
    public void getCollectionAsInputStream(PageQuality quality, BiConsumer<String, InputStream> fn) throws IOException {
        int pageIndex = 0;
        HttpGet request;
        Document document;
        ArrayList<String> linkList = new ArrayList<>();
        do {
            request = new HttpGet(PixivURL.PIXIV_USER_COLLECTION_PAGE.replace("{pageIndex}", Integer.toString(++pageIndex)));
            setCookieInRequest(request, cookieStore);
            log.debug("Request Link: " + request.getURI().toString());
            HttpResponse response = httpClient.execute(request);
            // 解析网页内容，获得所有的收藏信息
            document = Jsoup.parse(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
            Elements items = document.select(".display_editable_works .image-item a.work");
            List<String> hrefList = items.eachAttr("href");
            log.debug("第 {} 页获取到的图片项数量: {}", pageIndex, hrefList.size());
            if(hrefList.size() == 0) {
                break;
            }

            Gson gson = new Gson();
            for (String href : hrefList) {
                HttpGet linkApiRequest = createHttpGetRequest(PixivURL.PIXIV_ILLUST_API_URL.replace("{illustId}", href.substring(href.lastIndexOf("/") + 1)));
                log.debug(linkApiRequest.getURI().toString());
                HttpResponse httpResponse = httpClient.execute(linkApiRequest);
                JsonObject linkResult = gson.fromJson(EntityUtils.toString(httpResponse.getEntity()), JsonObject.class);
                if(linkResult.get("error").getAsBoolean()) {
                    log.error("接口返回错误信息: {}", linkResult.get("message").getAsString());
                    continue;
                }

                JsonArray linkArray = linkResult.get("body").getAsJsonArray();
                for (int i = 0; i < linkArray.size(); i++) {
                    JsonObject linkObject = linkArray.get(i).getAsJsonObject().get("urls").getAsJsonObject();
                    linkList.add(linkObject.get((quality == null ? PageQuality.ORIGINAL : quality).toString().toLowerCase()).getAsString());
                }
            }
        } while(!document.select(".pager-container>.next").isEmpty());
        log.trace("获取完成.");
        AtomicInteger count = new AtomicInteger(1);
        linkList.forEach(link -> {
            log.trace("Next Link [{}]: {}", count.getAndIncrement(), link);
            InputStream imageInputStream = null;
            int tryCount = 0;
            do {
                try {
                    imageInputStream = getImageAsInputStream(httpClient, link);
                } catch (IOException e) {
                    log.error("获取图片数据时发生异常", e);
                    if(++tryCount < 5) {
                        log.warn("即将重试[{} / 5]", tryCount);
                    }
                }
            } while(imageInputStream == null);

            try(InputStream imageInput = new BufferedInputStream(imageInputStream, 256 * 1024)) {
                log.trace("调用回调方法...");
                fn.accept(link, imageInput);
                log.trace("回调方法调用完成.");
            } catch (IOException e) {
                log.error("图片获取失败", e);
            }
        });
    }

    /**
     * 获取推荐插图
     * @param quality 图片质量
     * @param fn 回调函数
     * @throws IOException 当获取时发生异常则直接抛出
     */
    public void getRecommendAsInputStream(PageQuality quality, BiConsumer<String, InputStream> fn) throws IOException {
        HttpResponse response = httpClient.execute(createHttpGetRequest(PixivURL.PIXIV_INDEX_URL));
        Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));

        HttpClient imageClient = HttpClientBuilder.create().build();
        Elements elements = document.select(".gtm-illust-recommend-zone>.image-item>.gtm-illust-recommend-thumbnail-link");
        for(int illustIndex = 0; illustIndex < elements.size(); illustIndex++){
            String href = elements.get(illustIndex).attr("href");
            int illustId = Integer.parseInt(href.substring(href.lastIndexOf("/") + 1));
            log.debug("({}/{}) Href: {}, IllustID: {}", illustIndex + 1, elements.size(), href, illustId);
            List<String> pageLinkList = getIllustAllPageDownload(httpClient, this.cookieStore, illustId, quality);
            for (int linkIndex = 0; linkIndex < pageLinkList.size(); linkIndex++) {
                String link = pageLinkList.get(linkIndex);
                String fileName = link.substring(link.lastIndexOf("/") + 1);
                log.trace("({}/{}) 正在处理 {}", linkIndex, pageLinkList.size(), fileName);
                InputStream imageInputStream = null;
                int tryCount = 0;
                do {
                    try {
                        imageInputStream = getImageAsInputStream(imageClient, link);
                    } catch (IOException e) {
                        log.error("获取图片数据时发生异常", e);
                        if(++tryCount < 5) {
                            log.warn("即将重试[{} / 5]", tryCount);
                        }
                    }
                } while(imageInputStream == null);

                try(InputStream pageInputStream = new BufferedInputStream(imageInputStream, 256 * 1024)) {
                    fn.accept(fileName, pageInputStream);
                }
                log.trace("Done!");
            }
            log.debug("IllustId {} 处理完成.", illustId);
        }
    }

    /**
     * 获取排行榜
     * @param contentType 内容类型
     * @param mode 查询模式
     * @param time 查询时间
     * @param range 从第一名开始的范围
     * @param quality 图片质量
     * @param fn 回调函数
     * @throws IOException 当请求发生异常时抛出
     */
    public void getRankingAsInputStream(PixivURL.RankingContentType contentType, PixivURL.RankingMode mode,
                                        Date time, int range, PageQuality quality, RankingDownloadFunction fn) throws IOException {
        getRankingAsInputStream(contentType, mode, time, 1, range, quality, fn);
    }

    /**
     * 获取排行榜
     * @param contentType 内容类型
     * @param mode 查询模式
     * @param time 查询时间
     * @param rankStart 开始排行位(包括)
     * @param range 范围
     * @param quality 图片质量
     * @param fn 回调函数
     * @throws IOException 当请求发生异常时抛出
     */
    public void getRankingAsInputStream(PixivURL.RankingContentType contentType, PixivURL.RankingMode mode,
                                        Date time, int rankStart, int range, PageQuality quality, RankingDownloadFunction fn) throws IOException {
        getRanking(contentType, mode, time, rankStart, range).forEach(rankInfo -> {
            int rank = rankInfo.get("rank").getAsInt();
            int illustId = rankInfo.get("illust_id").getAsInt();
            int authorId = rankInfo.get("user_id").getAsInt();
            String authorName = rankInfo.get("user_name").getAsString();
            String title = rankInfo.get("title").getAsString();
            log.trace("当前到第 {}/{} 名(总共 {} 名), IllustID: {}, Author: ({}) {}, Title: {}", rank, rankStart + range - 1, range, illustId, authorId, authorName, title);
            log.trace("正在获取PagesLink...");
            List<String> linkList;
            try {
                linkList = getIllustAllPageDownload(httpClient, this.cookieStore, illustId, quality);
            } catch (IOException e) {
                if(e.getMessage().equals("該当作品は削除されたか、存在しない作品IDです。")) {
                    log.warn("作品 {} 不存在.", illustId);
                } else {
                    e.printStackTrace();
                }
                return;
            }
            log.debug("PagesLink 获取完成, 总数: {}", linkList.size());
            for (int pageIndex = 0; pageIndex < linkList.size(); pageIndex++) {
                String downloadLink = linkList.get(pageIndex);
                log.trace("当前Page: {}/{}", pageIndex + 1, linkList.size());
                try(InputStream imageInputStream = new BufferedInputStream(getImageAsInputStream(HttpClientBuilder.create().build(), downloadLink), 256 * 1024)) {
                    fn.download(rank, downloadLink, rankInfo.deepCopy(), imageInputStream);
                } catch(IOException e) {
                    log.error("下载插画时发生异常", e);
                    return;
                }
                log.trace("完成.");
            }
        });
    }

    /**
     * 从JsonArray获取数据
     * @param rankingList JsonArray对象
     * @param rankStart 开始索引, 从0开始
     * @param range 范围
     * @return 返回List对象
     */
    public static List<JsonObject> getRanking(List<JsonObject> rankingList, int rankStart, int range) {
        log.debug("正在读取JsonArray...(rankStart: {}, range: {})", rankStart, range);
        ArrayList<JsonObject> results = new ArrayList<>(rankingList.size());
        for (int rankIndex = rankStart; rankIndex < rankStart + range; rankIndex++) {
            JsonElement jsonElement = rankingList.get(rankIndex - rankStart);
            JsonObject rankInfo = jsonElement.getAsJsonObject();
            int rank = rankInfo.get("rank").getAsInt();
            int illustId = rankInfo.get("illust_id").getAsInt();
            int authorId = rankInfo.get("user_id").getAsInt();
            String authorName = rankInfo.get("user_name").getAsString();
            String title = rankInfo.get("title").getAsString();
            log.trace("Array-当前到第 {}/{} 名(总共 {} 名), IllustID: {}, Author: ({}) {}, Title: {}", rank, rankStart + range, range, illustId, authorId, authorName, title);
            results.add(rankInfo);
        }
        log.debug("JsonArray读取完成.");
        return results;
    }

    /**
     * 从JsonArray获取数据
     * @param rankingArray JsonArray对象
     * @param rankStart 开始索引, 从0开始
     * @param range 范围
     * @return 返回List对象
     */
    public static List<JsonObject> getRanking(JsonArray rankingArray, int rankStart, int range) {
        List<JsonObject> list = new Gson().fromJson(rankingArray, new TypeToken<List<JsonObject>>(){}.getType());
        return getRanking(list, rankStart, range);
    }

    /**
     * 获取排行榜.
     * <p>注意: 如果范围实际上没超出, 但返回排行榜不足, 会导致与实际请求的数量不符, 需要检查</p>
     * @param contentType 排行榜类型
     * @param mode 排行榜模式
     * @param time 查询时间
     * @param rankStart 开始排名, 从1开始
     * @param range 取范围
     * @return 成功返回有值List, 失败且无异常返回空
     * @throws IllegalArgumentException 当{@linkplain net.lamgc.cgj.pixiv.PixivURL.RankingContentType RankingContentType}
     *                                  与{@linkplain net.lamgc.cgj.pixiv.PixivURL.RankingMode RankingMode}互不兼容时抛出
     * @throws IndexOutOfBoundsException 当排行榜选取范围超出排行榜范围时抛出(排行榜范围为 1 ~ 500 名)
     * @throws IOException 当Http请求发生异常时抛出, 或Http请求响应码非200时抛出
     */
    public List<JsonObject> getRanking(PixivURL.RankingContentType contentType, PixivURL.RankingMode mode,
                                           Date time, int rankStart, int range) throws IOException {
        Objects.requireNonNull(time);
        if(!Objects.requireNonNull(contentType).isSupportedMode(Objects.requireNonNull(mode))) {
            throw new IllegalArgumentException("ContentType不支持指定的RankingMode: ContentType: " + contentType.name() + ", Mode: " + mode.name());
        } else if(rankStart <= 0) {
            throw new IndexOutOfBoundsException("rankStart cannot be less than or equal to zero: " + rankStart);
        } else if(range <= 0) {
            throw new IndexOutOfBoundsException("range cannot be less than or equal to zero:" + range);
        } else if(rankStart + range - 1 > 500) {
            throw new IndexOutOfBoundsException("排名选取范围超出了排行榜范围: rankStart=" + rankStart + ", range=" + range + ", length:" + (rankStart + range - 1));
        }

        int startPages = (int) Math.max(1, Math.floor(rankStart / 50F));
        int endPages = (int) Math.min(10, Math.ceil((rankStart + range) / 50F));
        int startIndex = rankStart - 1;
        int count = 0;
        Gson gson = new Gson();
        ArrayList<JsonObject> results = new ArrayList<>(range);
        boolean canNext = true;
        for (int pageIndex = startPages; canNext && pageIndex <= endPages && count < range; pageIndex++) {
            HttpGet request = createHttpGetRequest(PixivURL.getRankingLink(contentType, mode, time, pageIndex, true));
            log.trace("RequestUri: {}", request.getURI());
            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            log.trace("ResponseBody: {}", responseBody);
            if(response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Http Response Error: '" + response.getStatusLine() + "', ResponseBody: '" + responseBody + '\'');
            }

            JsonObject resultObject = gson.fromJson(responseBody, JsonObject.class);
            canNext = resultObject.get("next").getAsJsonPrimitive().isNumber();
            JsonArray resultArray = resultObject.getAsJsonArray("contents");
            for (int resultIndex = startIndex; resultIndex < resultArray.size() && count < range; resultIndex++, count++) {
                results.add(resultArray.get(resultIndex).getAsJsonObject());
            }

            // 重置索引
            startIndex = 0;
        }
        return results;
    }

    /**
     * 获取作品的预加载数据
     * @param illustId 作品id
     * @return 如果请求成功返回JsonObject, 失败返回null
     * @throws IOException 当请求响应非200或请求发生异常时抛出.
     */
    public JsonObject getIllustPreLoadDataById(int illustId) throws IOException {
        HttpGet request = createHttpGetRequest(PixivURL.getPixivRefererLink(illustId));
        HttpResponse response = httpClient.execute(request);

        if(response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Http响应码非200: " + response.getStatusLine());
        }

        Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));
        Elements selectElements = document.select("#meta-preload-data");
        if(selectElements.size() == 0) {
            return null;
        }

        return new Gson().fromJson(selectElements.attr("content"), JsonObject.class);
    }


    @FunctionalInterface
    public interface RankingDownloadFunction {
        /**
         * 接收图片InputStream
         * @param rank 当前作品排名
         * @param link 作品下载链接
         * @param inputStream 作品下载输入流, InputStream会自动关闭
         */
        void download(int rank, String link, JsonObject rankInfo, InputStream inputStream);
    }

    public HttpGet createHttpGetRequest(String url) {
        HttpGet request = new HttpGet(url);
        setCookieInRequest(request, cookieStore);
        return request;
    }


    /**
     * 取Illust所有页的原图下载链接
     * @param httpClient 用于发起请求的HttpClient对象
     * @param illustId 插画ID
     * @param quality 页质量, 见{@link PageQuality}
     * @return 返回该illust所有Page的下载链接
     * @throws IOException 当HttpClient在请求时发生异常, 或接口报错时抛出, 注意{@link IOException#getMessage()}
     */
    public static List<String> getIllustAllPageDownload(HttpClient httpClient, CookieStore cookieStore, int illustId, PageQuality quality) throws IOException {
        HttpGet linkApiRequest = new HttpGet(PixivURL.PIXIV_ILLUST_API_URL.replace("{illustId}", Integer.toString(illustId)));
        setCookieInRequest(linkApiRequest, cookieStore);
        HttpResponse response = httpClient.execute(linkApiRequest);
        JsonObject resultObject = new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);

        if(resultObject.get("error").getAsBoolean()) {
            String message = resultObject.get("message").getAsString();
            log.warn("作品页面接口请求错误, 错误信息: {}", message);
            throw new IOException(message);
        }

        JsonArray linkArray = resultObject.getAsJsonArray("body");

        ArrayList<String> resultList = new ArrayList<>();
        String qualityType = quality == null ? "original" : quality.toString().toLowerCase();
        log.debug("已选择插画类型: {}", qualityType);
        linkArray.forEach(el -> {
            JsonObject urlObj = el.getAsJsonObject().getAsJsonObject("urls");
            resultList.add(urlObj.get(qualityType).getAsString());
        });

        return resultList;
    }

    /**
     * 插图质量
     */
    public enum PageQuality{
        /**
         * 原图画质
         */
        ORIGINAL,
        /**
         * 常规画质
         */
        REGULAR,
        /**
         * 小图画质
         */
        SMALL,
        /**
         * 迷你画质
         */
        THUMB_MINI
    }

    /**
     * 获取帐号所有的收藏插画，并以输入流形式提供
     * @return 获取所有链接的InputStream, 请注意关闭InputStream
     * @throws IOException 当获取时发生异常则直接抛出
     * @deprecated 该方法可能会导致已经打开的InputStream超时, 使图片获取失败,
     *          请直接使用{@linkplain #getCollectionAsInputStream(PageQuality, BiConsumer)}
     */
    @Deprecated
    public Set<Map.Entry<String, InputStream>> getCollectionAsInputStream(PageQuality quality) throws IOException {
        HashSet<Map.Entry<String, InputStream>> illustInputStreamSet = new HashSet<>();
        getCollectionAsInputStream(quality, (link, inputStream) -> illustInputStreamSet.add(new AbstractMap.SimpleEntry<>(link, inputStream)));
        return illustInputStreamSet;
    }


    /**
     * 获取Pixiv图片
     * @param httpClient HttpClient对象
     * @param link Pixiv图片链接
     * @return 返回图片InputStream，注意关闭InputStream
     * @throws IOException 获取失败时抛出
     * @throws IllegalArgumentException 当链接无法处理时抛出
     */
    public static InputStream getImageAsInputStream(HttpClient httpClient, String link) throws IOException {
        HttpGet request = new HttpGet(link);
        int startIndex = link.lastIndexOf("/");
        int endIndex = link.lastIndexOf("_");
        if(startIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException("无法从链接获取illustID: " + link);
        }

        String referer = PixivURL.getPixivRefererLink(link.substring(startIndex + 1, endIndex));
        request.addHeader(HttpHeaderNames.REFERER.toString(), referer);

        HttpResponse response = httpClient.execute(request);
        log.trace("response: {}", response);
        log.trace("Content Length: {}KB",
                Float.parseFloat(response.getFirstHeader(HttpHeaderNames.CONTENT_LENGTH.toString()).getValue()) / 1024F
        );
        log.trace(response.getFirstHeader(HttpHeaderNames.CONTENT_TYPE.toString()).toString());
        return response.getEntity().getContent();
    }

    /**
     * 登出当前会话.<br/>
     * 登出成功后, 该Cookies作废.
     * @return 返回是否成功登出
     * @throws IOException 当登出请求异常时抛出
     */
    public boolean logOut() throws IOException {
        HttpGet request = new HttpGet(PixivURL.PIXIV_LOGOUT_URL);
        request.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
        setCookieInRequest(request, cookieStore);
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == 302) {
            cookieStore.clear();
            return true;
        } else {
            return false;
        }

    }

    /**
     * 获取作品信息
     * @param illustId 作品ID
     * @return 成功获取返回JsonObject, 失败返回null, <br/>
     *      Json示例: <br/>
     *      <pre>
     *          {
     *         "illustId": "79584670",
     *         "illustTitle": "このヤンキーはウブすぎる",
     *         "id": "79584670",
     *         "title": "このヤンキーはウブすぎる",
     *         "illustType": 1,
     *         "xRestrict": 0,
     *         "restrict": 0,
     *         "sl": 2,
     *         "url": "https://i.pximg.net/c/360x360_70/img-master/img/2020/02/19/00/38/23/79584670_p0_square1200.jpg",
     *         "description": "",
     *         "tags": [
     *           "漫画",
     *           "オリジナル",
     *           "創作",
     *           "創作男女",
     *           "コロさん、ポリさん此方です!",
     *           "恋の予感",
     *           "あまずっぺー",
     *           "交換日記",
     *           "続編希望!!",
     *           "オリジナル10000users入り"
     *         ],
     *         "userId": "4778293",
     *         "userName": "隈浪さえ",
     *         "width": 3288,
     *         "height": 4564,
     *         "pageCount": 4,
     *         "isBookmarkable": true,
     *         "bookmarkData": null,
     *         "alt": "#オリジナル このヤンキーはウブすぎる - 隈浪さえ的漫画",
     *         "isAdContainer": false,
     *         "profileImageUrl": "https://i.pximg.net/user-profile/img/2019/12/04/18/56/19/16639046_fea29ce38ea89b0cb2313b40b3a72f9a_50.jpg",
     *         "type": "illust"
     *       }
     *      </pre>
     * @throws IOException 当请求发生异常, 或接口返回错误信息时抛出.
     * @throws NoSuchElementException 当该作品不存在时抛出异常
     */
    public JsonObject getIllustInfoByIllustId(int illustId) throws IOException, NoSuchElementException {
        HttpGet request = createHttpGetRequest(PixivURL.getPixivIllustInfoAPI(illustId));
        HttpResponse response = httpClient.execute(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        log.trace("Response Content: {}", responseStr);
        JsonObject responseObj = new Gson().fromJson(responseStr, JsonObject.class);

        if(responseObj.get("error").getAsBoolean()) {
            throw new IOException(responseObj.get("message").getAsString());
        }

        JsonArray illustsArray = responseObj.getAsJsonObject("body").getAsJsonArray("illusts");
        if(illustsArray.size() == 1) {
            return illustsArray.get(0).getAsJsonObject();
        } else {
            throw new NoSuchElementException("No work found: " + illustId);
        }
    }


    public static void setCookieInRequest(HttpRequest request, CookieStore cookieStore) {
        StringBuilder builder = new StringBuilder();
        cookieStore.getCookies().forEach(cookie -> builder.append(cookie.getName()).append("=").append(cookie.getValue()).append("; "));
        request.setHeader(HttpHeaderNames.COOKIE.toString(), builder.toString());
    }

    public enum PixivIllustType {
        /**
         * 插画
         */
        ILLUST(0),

        /**
         * 漫画
         */
        MANGA(1),

        /**
         * 动图
         */
        UGOIRA(2),
        ;

        public final int typeId;

        PixivIllustType(int typeId) {
            this.typeId = typeId;
        }

        /**
         * 通过typeId搜索PixivIllustType
         * @param typeId 接口属性"illustType"的值
         * @return 如果搜索到匹配的IllustType则返回, 未找到返回null
         */
        public static PixivIllustType getIllustTypeById(int typeId) {
            for (PixivIllustType illustType : PixivIllustType.values()) {
                if(illustType.typeId == typeId) {
                    return illustType;
                }
            }
            return null;
        }

        /**
         * 通过预加载数据获取作品类型
         * @param illustId 作品Id
         * @param preLoadDataObject 预加载数据(IllustInfo也可以)
         * @return 如果存在illustType属性, 则返回对应项, 如没有, 或数据内不存在指定作品id的数据, 返回null
         */
        public static PixivIllustType getIllustTypeByPreLoadData(int illustId, JsonObject preLoadDataObject) {
            JsonObject illustData = null;
            if(preLoadDataObject.has("illust")) {
                illustData = preLoadDataObject.getAsJsonObject("illust").getAsJsonObject(String.valueOf(illustId));
            } else if(preLoadDataObject.has(String.valueOf(illustId))) {
                illustData = preLoadDataObject.getAsJsonObject(String.valueOf(illustId));
            } else if(preLoadDataObject.has("body")) { // 解析IllustInfo
                for (JsonElement jsonElement : preLoadDataObject.getAsJsonObject("body").getAsJsonArray("illusts")) {
                    JsonObject illustInfo = jsonElement.getAsJsonObject();
                    if (illustInfo.get("illustId").getAsInt() == illustId) {
                        illustData = illustInfo;
                        break;
                    }
                }
                if(illustData == null) {
                    return null;
                }
            } else {
                illustData = preLoadDataObject;
            }

            if(illustData.get("illustId").getAsInt() != illustId) {
                return null;
            }

            if(illustData.has("illustType")) {
                return getIllustTypeById(illustData.get("illustType").getAsInt());
            } else {
                return null;
            }
        }

    }

}
