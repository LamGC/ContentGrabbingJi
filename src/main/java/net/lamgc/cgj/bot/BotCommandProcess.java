package net.lamgc.cgj.bot;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lamgc.cgj.bot.boot.BotGlobal;
import net.lamgc.cgj.bot.cache.CacheStore;
import net.lamgc.cgj.bot.cache.CacheStoreCentral;
import net.lamgc.cgj.bot.cache.JsonRedisCacheStore;
import net.lamgc.cgj.bot.event.BufferedMessageSender;
import net.lamgc.cgj.bot.sort.PreLoadDataAttribute;
import net.lamgc.cgj.bot.sort.PreLoadDataAttributeComparator;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivDownload.PageQuality;
import net.lamgc.cgj.pixiv.PixivSearchAttribute;
import net.lamgc.cgj.pixiv.PixivSearchLinkBuilder;
import net.lamgc.cgj.pixiv.PixivURL;
import net.lamgc.cgj.util.PixivUtils;
import net.lamgc.utils.base.runner.Argument;
import net.lamgc.utils.base.runner.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"SameParameterValue"})
public class BotCommandProcess {

    private final static Logger log = LoggerFactory.getLogger(BotCommandProcess.class);

    /**
     * 作品报告存储 - 不过期
     */
    public final static CacheStore<JsonElement> reportStore =
            new JsonRedisCacheStore(BotGlobal.getGlobal().getRedisServer(),
                    "report", BotGlobal.getGlobal().getGson());

    private final static RankingUpdateTimer updateTimer = new RankingUpdateTimer();

    private final static int SEARCH_PAGE_MAX_ITEM = 60;

    public static void initialize() {
        log.info("正在初始化...");

        SettingProperties.loadProperties();

        updateTimer.schedule(null);
        log.info("初始化完成.");
    }

    /**
     * 执行定时更新任务
     * @param queryTime 指定更新日期
     */
    public static void runUpdateTimer(Date queryTime) {
        log.info("正在手动触发排行榜更新任务...");
        updateTimer.now(queryTime);
        log.info("任务执行结束.");
    }

    @Command(defaultCommand = true)
    public static String help() {
        return "CGJ Bot使用指南" + "\n" +
                "使用方法：.cgj <命令> [参数...]" + "\n" +
                "例如查询作品信息功能：" + "\n" +
                ".cgj info -id 80846159" + "\n" +
                "目前可用的命令:" + "\n" +
                "\t" + "ranking - 获取今天或指定日期排行榜的前10名作品" + "\n" +
                "\t\t" + "-date - 指定查询日期(年-月-日)" + "\n" +
                "\t\t" + "-type - 排行榜类型(illust/插画, ugoira/动图, manga/漫画)" + "\n" +
                "\t" + "search - 搜索指定关键词并显示前10个作品" + "\n" +
                "\t\t" + "-content - 搜索内容" + "\n" +
                "\t" + "link - 获取作品的Pixiv页面" + "\n" +
                "\t\t" + "-id - 作品id" + "\n" +
                "\t" + "info - 获取Pixiv作品信息" + "\n" +
                "\t\t" + "-id - 作品id" + "\n" +
                "\t" + "image - 获取指定作品的图片" + "\n" +
                "\t\t" + "-id - 作品id" + "\n" +
                "\t\t" + "-quality - 图片质量(original/原图 regular/预览图)" + "\n" +
                "\t\t" + "-page - 页数" + "\n" +
                "\t" + "report - 报告不当作品" + "\n" +
                "\t\t" + "-id - 作品Id" + "\n" +
                "\t\t" + "-msg - 报告原因" + "\n";
    }

    /**
     * 作品信息查询
     * @param fromGroup 来源群(系统提供)
     * @param illustId 作品Id
     * @return 返回作品信息
     */
    @Command(commandName = "info")
    public static String artworkInfo(@Argument(name = "$fromGroup") long fromGroup,
                                     @Argument(name = "id") int illustId)
    throws InterruptedException {
        if(illustId <= 0) {
            return "这个作品Id是错误的！";
        }

        try {
            if(isNoSafe(illustId, SettingProperties.getProperties(fromGroup), false) || isReported(illustId)) {
                return "阅览禁止：该作品已被封印！！";
            }

            JsonObject illustPreLoadData = CacheStoreCentral.getCentral().getIllustPreLoadData(illustId, false);
            // 在 Java 6 开始, 编译器会将用'+'进行的字符串拼接将自动转换成StringBuilder拼接
            return "色图姬帮你了解了这个作品的信息！\n" + "---------------- 作品信息 ----------------" +
                    "\n作品Id: " + illustId +
                    "\n作品标题：" + illustPreLoadData.get("illustTitle").getAsString() +
                    "\n作者(作者Id)：" + illustPreLoadData.get("userName").getAsString() +
                    "(" + illustPreLoadData.get("userId").getAsInt() + ")" +
                    "\n点赞数：" + illustPreLoadData.get(PreLoadDataAttribute.LIKE.attrName).getAsInt() +
                    "\n收藏数：" + illustPreLoadData.get(PreLoadDataAttribute.BOOKMARK.attrName).getAsInt() +
                    "\n围观数：" + illustPreLoadData.get(PreLoadDataAttribute.VIEW.attrName).getAsInt() +
                    "\n评论数：" + illustPreLoadData.get(PreLoadDataAttribute.COMMENT.attrName).getAsInt() +
                    "\n页数：" + illustPreLoadData.get(PreLoadDataAttribute.PAGE.attrName).getAsInt() + "页" +
                    "\n作品链接：" + artworksLink(fromGroup, illustId) + "\n" +
                    "---------------- 作品图片 ----------------\n" +
                    CacheStoreCentral.getCentral().getImageById(fromGroup, illustId, PageQuality.REGULAR, 1) + "\n" +
                    "使用 \".cgj image -id " +
                    illustId +
                    "\" 获取原图。\n如有不当作品，可使用\".cgj report -id " +
                    illustId + "\"向色图姬反馈。";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "尚未支持";
    }

    /**
     * 排行榜命令
     * @param fromGroup 来源群(系统提供)
     * @param queryTime 查询时间, 格式: 年-月-日
     * @param force 是否强制查询, 当主动提供的时间不在查询范围时, 是否强制查询, 仅系统可用
     * @param contentMode 内容模式
     * @param contentType 排行榜类型
     * @return 返回排行榜信息
     */
    @Command
    public static String ranking(
            @Argument(name = "$fromGroup") long fromGroup,
            @Argument(force = false, name = "date") Date queryTime,
            @Argument(force = false, name = "force") boolean force,
            @Argument(force = false, name = "mode", defaultValue = "DAILY") String contentMode,
            @Argument(force = false, name = "type", defaultValue = "ALL") String contentType,
            @Argument(force = false, name = "p", defaultValue = "1") int pageIndex
    ) throws InterruptedException {
        if(pageIndex <= 0) {
            return "色图姬找不到指定页数的排行榜!";
        }

        Date queryDate = queryTime;
        if (queryDate == null) {
            queryDate = new Date();
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            log.debug("CurrentDate: {}", queryDate);
            gregorianCalendar.setTime(queryDate);
            if (gregorianCalendar.get(Calendar.HOUR_OF_DAY) < 12) {
                gregorianCalendar.add(Calendar.DATE, -2);
            } else {
                gregorianCalendar.add(Calendar.DATE, -1);
            }
            queryDate = gregorianCalendar.getTime();
        } else {
            if(new Date().before(queryDate) && !force) {
                log.warn("查询的日期过早, 无法查询排行榜.");
                return "查询日期过早, 暂未更新指定日期的排行榜!";
            }
        }

        PixivURL.RankingMode mode;
        try {
            String rankingModeValue = contentMode.toUpperCase();
            mode = PixivURL.RankingMode.valueOf(rankingModeValue.startsWith("MODE_") ?
                    rankingModeValue : "MODE_" + rankingModeValue);
        } catch (IllegalArgumentException e) {
            log.warn("无效的RankingMode值: {}", contentMode);
            return "参数无效, 请查看帮助信息";
        }

        PixivURL.RankingContentType type;
        try {
            String contentTypeValue = contentType.toUpperCase();
            type = PixivURL.RankingContentType.valueOf(
                    contentTypeValue.startsWith("TYPE_") ? contentTypeValue : "TYPE_" + contentTypeValue);
        } catch (IllegalArgumentException e) {
            log.warn("无效的RankingContentType值: {}", contentType);
            return "参数无效, 请查看帮助信息";
        }

        if(!type.isSupportedMode(mode)) {
            log.warn("RankingContentType不支持指定的RankingMode.(ContentType: {}, RankingMode: {})",
                    type.name(), mode.name());
            return "不支持的内容类型或模式!";
        }

        StringBuilder resultBuilder = new StringBuilder(mode.name() + " - 以下是 ")
                .append(new SimpleDateFormat("yyyy-MM-dd").format(queryDate)).append(" 的Pixiv插画排名榜前十名：\n");
        try {
            int index = 0;
            int itemLimit = 10;
            String itemLimitPropertyKey = "ranking.itemCountLimit";
            try {
                itemLimit = Integer.parseInt(SettingProperties
                        .getProperty(fromGroup, itemLimitPropertyKey, "10"));
            } catch(NumberFormatException e) {
                log.warn("配置项 {} 的参数值格式有误!", itemLimitPropertyKey);
            }

            int imageLimit = 3;
            String imageLimitPropertyKey = "ranking.imageCountLimit";
            try {
                imageLimit = Integer.parseInt(
                        SettingProperties.getProperty(fromGroup, imageLimitPropertyKey, "3"));
            } catch(NumberFormatException e) {
                log.warn("配置项 {} 的参数值格式有误!", imageLimitPropertyKey);
            }

            int startsIndex = itemLimit * pageIndex - (itemLimit - 1);
            List<JsonObject> rankingInfoList = CacheStoreCentral.getCentral()
                    .getRankingInfoByCache(type, mode, queryDate,
                            Math.max(1, startsIndex), Math.max(0, itemLimit), false);
            if(rankingInfoList.isEmpty()) {
                return "无法查询排行榜，可能排行榜尚未更新。";
            }

            for (JsonObject rankInfo : rankingInfoList) {
                index++;
                int rank = rankInfo.get("rank").getAsInt();
                int illustId = rankInfo.get("illust_id").getAsInt();
                int authorId = rankInfo.get("user_id").getAsInt();
                int pagesCount = rankInfo.get("illust_page_count").getAsInt();
                String authorName = rankInfo.get("user_name").getAsString();
                String title = rankInfo.get("title").getAsString();
                resultBuilder.append(rank).append(". (id: ").append(illustId).append(") ").append(title)
                        .append("(Author: ").append(authorName).append(",").append(authorId).append(") ")
                        .append(pagesCount).append("p.\n");
                if (index <= imageLimit) {
                    resultBuilder
                            .append(CacheStoreCentral.getCentral()
                                    .getImageById(fromGroup, illustId, PixivDownload.PageQuality.REGULAR, 1))
                            .append("\n");
                }
            }
        } catch (IOException e) {
            log.error("消息处理异常", e);
            return "排名榜获取失败！详情请查看机器人控制台。";
        }
        return resultBuilder.append("如查询当前时间获取到昨天时间，则今日排名榜未更新。\n" +
                "如有不当作品，可使用\".cgj report -id 作品id\"向色图姬反馈。").toString();
    }

    /**
     * 查询指定作者的作品(尚未完成)
     * @return 返回作者信息和部分作品
     */
    @Command(commandName = "userArt")
    public static String userArtworks() {

        return "功能未完成";
    }

    /**
     * 随机获取一副作品
     */
    @Command(commandName = "random")
    public static String randomImage(
            @Argument(name = "$fromGroup") long fromGroup,
            @Argument(force = false, name = "mode", defaultValue = "DAILY") String contentMode,
            @Argument(force = false, name = "type", defaultValue = "ILLUST") String contentType) {
        PixivURL.RankingMode mode;
        try {
            String rankingModeValue = contentMode.toUpperCase();
            mode = PixivURL.RankingMode.valueOf(rankingModeValue.startsWith("MODE_") ?
                    rankingModeValue : "MODE_" + rankingModeValue);
        } catch (IllegalArgumentException e) {
            log.warn("无效的RankingMode值: {}", contentMode);
            return "参数无效, 请查看帮助信息";
        }

        PixivURL.RankingContentType type;
        try {
            String contentTypeValue = contentType.toUpperCase();
            type = PixivURL.RankingContentType.valueOf(
                    contentTypeValue.startsWith("TYPE_") ? contentTypeValue : "TYPE_" + contentTypeValue);
        } catch (IllegalArgumentException e) {
            log.warn("无效的RankingContentType值: {}", contentType);
            return "参数无效, 请查看帮助信息";
        }

        BufferedMessageSender bufferedSender = new BufferedMessageSender();
        RandomRankingArtworksSender artworksSender = 
            new RandomRankingArtworksSender(bufferedSender, fromGroup, 1, 200, mode, type,
                    PageQuality.ORIGINAL);
        artworksSender.send();
        return bufferedSender.getBufferContent();
    }

    /**
     * 搜索命令
     * @param fromGroup 来源群(系统提供)
     * @param content 搜索内容
     * @param type 搜索类型
     * @param area 搜索区域
     * @param includeKeywords 包括关键字
     * @param excludeKeywords 排除关键字
     * @param contentOption 搜索选项
     * @param pagesIndex 搜索页索引
     * @return 返回搜索内容消息
     * @throws IOException 当搜索发生异常时抛出
     */
    @Command
    public static String search(
            @Argument(name = "$fromGroup") long fromGroup,
            @Argument(name = "content") String content,
            @Argument(name = "type", force = false) String type,
            @Argument(name = "area", force = false) String area,
            @Argument(name = "in", force = false) String includeKeywords,
            @Argument(name = "ex", force = false) String excludeKeywords,
            @Argument(name = "option", force = false) String contentOption,
            @Argument(name = "p", force = false, defaultValue = "1") int pagesIndex
    ) throws IOException, InterruptedException {
        PixivSearchLinkBuilder searchLinkBuilder = PixivUtils.buildSearchLinkBuilderFromString(content, type, area,
                        includeKeywords, excludeKeywords, contentOption, pagesIndex);
        log.debug("正在执行搜索...");
        int limit = 8;
        try {
            limit = Integer.parseInt(SettingProperties.
                    getProperty(fromGroup, "search.itemCountLimit", "8"));
        } catch (Exception e) {
            log.warn("参数转换异常!将使用默认值(" + limit + ")", e);
        }

        int totalCount = 0;
        StringBuilder result = new StringBuilder("内容 " + content + " 的搜索结果：\n");
        List<JsonObject> artworkList = getSearchResult(searchLinkBuilder, fromGroup, limit, pagesIndex);
        artworkList.sort(new PreLoadDataAttributeComparator(PreLoadDataAttribute.LIKE));
        int startIndex = limit * pagesIndex - limit + 1;
        int pageStartIndex = startIndex % SEARCH_PAGE_MAX_ITEM;
        for(int index = pageStartIndex; totalCount < limit && index < artworkList.size(); index++) {
            int illustId = artworkList.get(index - 1).get("illustId").getAsInt();
            // 预加载数据有更多信息可以提供
            JsonObject artworkPreLoadData = CacheStoreCentral.getCentral()
                    .getIllustPreLoadData(illustId, false);

            // 构造消息内容
            result.append(startIndex++).append(". （").append(artworkPreLoadData.get("illustId").getAsInt()).append("） ")
                    .append(artworkPreLoadData.get("illustTitle").getAsString());
            result.append("\n\t").append("作者：").append(artworkPreLoadData.get("userName").getAsString());
            result.append("\n\t").append("作品页数：").append(artworkPreLoadData.get("pageCount").getAsInt()).append(" 页");
            result.append("\n\t").append("点赞数：")
                    .append(artworkPreLoadData.get(PreLoadDataAttribute.LIKE.attrName).getAsInt()).append(" 页");
            result.append("\n\t").append("收藏数：")
                    .append(artworkPreLoadData.get(PreLoadDataAttribute.BOOKMARK.attrName).getAsInt()).append(" 页");
            result.append("\n\t").append("围观数：")
                    .append(artworkPreLoadData.get(PreLoadDataAttribute.VIEW.attrName).getAsInt()).append(" 页");
            result.append("\n\t").append("评论数：")
                    .append(artworkPreLoadData.get(PreLoadDataAttribute.COMMENT.attrName).getAsInt()).append(" 页");
            result.append(CacheStoreCentral.getCentral()
                    .getImageById(fromGroup, illustId, PageQuality.REGULAR, 1)).append("\n");
            totalCount++;
        }

        return totalCount <= 0 ?
                "搜索完成，未找到相关作品。" :
                Strings.nullToEmpty(result.toString()) + "预览图片并非原图，使用“.cgj image -id 作品id”获取原图\n" +
                "如有不当作品，可使用\".cgj report -id 作品id\"向色图姬反馈。";
    }

    /**
     * 获取 length 涉及的多个页的搜索结果.
     * 例如:
     *  pageMaxItem = 60;
     *  length = 150;
     *  page = 3;
     * 那么:
     *  endItemIndex = length * page = 450;
     *  startItemIndex = endItemIndex - length + 1 = 301;
     *  startPageIndex = ceil(startItemIndex / pageMaxItem) = 6;
     *  pageRange = ceil((endItemIndex - length + 1) / pageMaxItem) = 3;
     *  endPageIndex = startPageIndex - pageRange - 1 = 8;
     * 该方法将会取搜索结果的 6 ~ 8 页结果并返回;
     * @param searchLinkBuilder 已构造好除 Page 参数外其他参数的 {@link PixivSearchLinkBuilder}
     * @param length 所需结果的范围
     * @param page 所需结果的页数
     * @return 返回包含范围的涉及页面所有搜索结果.
     * @throws IOException 如获取发生异常则抛出.
     */
    private static List<JsonObject> getSearchResult(PixivSearchLinkBuilder searchLinkBuilder, long groupId, int length, int page) throws IOException {
        List<JsonObject> artworkList = new ArrayList<>(length);

        int endsItemIndex = length * page;
        int startsItemIndex = endsItemIndex - length + 1;
        int startPageIndex = (int) Math.ceil(startsItemIndex / (double) SEARCH_PAGE_MAX_ITEM);
        int pageRange = (int) Math.ceil((endsItemIndex - startsItemIndex) / (double) SEARCH_PAGE_MAX_ITEM);
        PixivSearchAttribute areaAttribute = PixivSearchAttribute.valueOf(searchLinkBuilder.getSearchArea().toString());
        Properties properties = SettingProperties.getProperties(groupId);
        int expectedQuantity = pageRange * SEARCH_PAGE_MAX_ITEM;
        for(int pageIndex = startPageIndex;
            pageIndex <= startPageIndex + pageRange - 1 || artworkList.size() < length || artworkList.size() < expectedQuantity;
            pageIndex++) {
            searchLinkBuilder.setPage(pageIndex);
            JsonObject searchBody = CacheStoreCentral.getCentral().getSearchBody(searchLinkBuilder);
            for(String areaAttributeName : areaAttribute.attributeNames) {
                JsonObject areaResult = searchBody.getAsJsonObject(areaAttributeName);
                if(areaResult == null || !areaResult.has("data")) {
                    log.debug("作品类型属性 {} 无搜索结果, 跳过...", areaAttributeName);
                    continue;
                }

                JsonArray areaArray = areaResult.getAsJsonArray("data");
                for(JsonElement element : areaArray) {
                    JsonObject artworkInfo = element.getAsJsonObject();
                    if(!artworkInfo.has("illustId")) {
                        log.warn("发现未含有illustId的JsonObject: '{}'", artworkInfo.toString());
                        continue;
                    }
                    final int illustId = artworkInfo.get("illustId").getAsInt();
                    if(isNoSafe(illustId, properties, false)) {
                        log.warn("作品 {} 为R18作品, 跳过.", illustId);
                        continue;
                    } else if(isReported(illustId)) {
                        log.warn("作品 {} 被报告, 跳过.", illustId);
                        continue;
                    }
                    artworkList.add(artworkInfo);
                }
            }
        }

        // 去重
        Set<JsonObject> hashSet = new HashSet<>(artworkList.size());
        hashSet.addAll(artworkList);
        artworkList.clear();
        artworkList.addAll(hashSet);
        return artworkList;
    }

    /**
     * 获取作品页面的下载链接
     * @param illustId 作品Id
     * @param quality 画质类型
     * @return 返回作品所有页面在Pixiv的下载链接(有防盗链, 考虑要不要设置镜像站)
     */
    @Command(commandName = "pages")
    public static String getPagesList(
            @Argument(name = "$fromGroup") long fromGroup,
            @Argument(name = "id") int illustId,
            @Argument(name = "quality", force = false) PixivDownload.PageQuality quality) {
        try {
            if(isNoSafe(illustId, SettingProperties.getProperties(fromGroup), false)) {
                log.warn("来源群 {} 查询的作品Id {} 为R18作品, 根据配置设定, 屏蔽该作品.", fromGroup, illustId);
                return "该作品已被封印！";
            }
            List<String> pagesList =
                    PixivDownload.getIllustAllPageDownload(
                            BotGlobal.getGlobal().getPixivDownload().getHttpClient(),
                            BotGlobal.getGlobal().getPixivDownload().getCookieStore(),
                            illustId, quality);
            StringBuilder builder = new StringBuilder("作品ID ").append(illustId)
                    .append(" 共有").append(pagesList.size()).append("页：").append("\n");
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

    /**
     * 获取作品链接
     * @param fromGroup 来源群(系统提供)
     * @param illustId 作品Id
     * @return 返回作品在Pixiv的链接
     */
    @Command(commandName = "link")
    public static String artworksLink(@Argument(name = "$fromGroup") long fromGroup,
                                      @Argument(name = "id") int illustId) {
        try {
            if (isNoSafe(illustId, SettingProperties.getProperties(fromGroup), false)) {
                log.warn("作品Id {} 已被屏蔽.", illustId);
                return "由于相关设置，该作品已被屏蔽！";
            } else if(isReported(illustId)) {
                log.warn("作品Id {} 被报告, 正在等待审核, 跳过该作品.", illustId);
                return "该作品暂时被封印，请等待色图姬进一步审核！";
            }
        } catch (IOException e) {
            log.error("获取作品信息失败!", e);
            return "作品信息无法获取！";
        }
        return PixivURL.getPixivRefererLink(illustId);
    }

    static void clearCache() {
        log.warn("正在清除所有缓存...");
        CacheStoreCentral.getCentral().clearCache();
        File imageStoreDir = new File(BotGlobal.getGlobal().getDataStoreDir(), "data/image/cgj/");
        File[] listFiles = imageStoreDir.listFiles();
        if (listFiles == null) {
            log.debug("图片缓存目录为空或内部文件获取失败!");
        } else {
            for (File file : listFiles) {
                log.debug("图片文件 {} 删除: {}", file.getName(), file.delete());
            }
        }
        log.debug("图片缓存目录删除: {}", imageStoreDir.delete());
        log.warn("缓存删除完成.");
    }

    @Command(commandName = "image")
    public static String getImageById(
            @Argument(name = "$fromGroup") long fromGroup,
            @Argument(name = "id") int illustId,
            @Argument(name = "quality", force = false) PixivDownload.PageQuality quality,
            @Argument(name = "p", force = false, defaultValue = "1") int pageIndex
    ) throws InterruptedException {
        return CacheStoreCentral.getCentral().getImageById(fromGroup, illustId, quality, pageIndex);
    }

    /**
     * 举报某一作品
     * @param fromGroup 来源群(系统提供)
     * @param illustId 需要举报的作品id
     * @param reason 举报原因
     * @return 返回提示信息
     */
    @Command
    public static String report(
            @Argument(name = "$fromGroup") long fromGroup,
            @Argument(name = "$fromQQ") long fromQQ,
            @Argument(name = "id") int illustId,
            @Argument(name = "msg", force = false) String reason
    ) {
        log.warn("收到作品反馈(IllustId: {}, 原因: {})", illustId, reason);
        JsonObject reportJson = new JsonObject();
        reportJson.addProperty("illustId", illustId);
        reportJson.addProperty("reportTime", new Date().getTime());
        reportJson.addProperty("fromGroup", fromGroup);
        reportJson.addProperty("fromQQ", fromQQ);
        reportJson.addProperty("reason", reason);
        reportStore.update(String.valueOf(illustId), reportJson, 0);
        return "色图姬收到了你的报告，将屏蔽该作品并对作品违规情况进行核实，感谢你的反馈！";
    }

    /**
     * 检查某一作品是否被报告
     * @param illustId 作品Id
     * @return 如果被报告了, 返回true
     */
    public static boolean isReported(int illustId) {
        return reportStore.exists(String.valueOf(illustId));
    }

    /**
     * Tag过滤表达式
     */
    private final static Pattern tagPattern = Pattern.compile(".*R-*18.*");
    /**
     * 检查指定作品是否为r18
     * @param illustId 作品Id
     * @param settingProp 配置项
     * @param returnRaw 是否返回原始值
     * @return 如果为true, 则不为全年龄
     * @throws IOException 获取数据时发生异常时抛出
     * @throws NoSuchElementException 当作品不存在时抛出
     */
    public static boolean isNoSafe(int illustId, Properties settingProp, boolean returnRaw)
            throws IOException, NoSuchElementException {
        JsonObject illustInfo = CacheStoreCentral.getCentral().getIllustInfo(illustId, false);
        JsonArray tags = illustInfo.getAsJsonArray("tags");
        boolean rawValue = illustInfo.get("xRestrict").getAsInt() != 0;
        if(!rawValue) {
            for(JsonElement tag : tags) {
                boolean current = tagPattern.matcher(tag.getAsString()).matches();
                if (current) {
                    rawValue = true;
                    break;
                }
            }
        }
        return returnRaw || settingProp == null ? rawValue :
                rawValue && !settingProp.getProperty("image.allowR18", "false")
                        .equalsIgnoreCase("true");
    }

}
