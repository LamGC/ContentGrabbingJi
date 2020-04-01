package net.lamgc.cgj.pixiv;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 目前已整理的一些Pixiv接口列表
 */
public class PixivURL {


    public static final String PIXIV_INDEX_URL = "https://www.pixiv.net";

    /**
     * P站预登陆url
     */
    public static final String PIXIV_LOGIN_PAGE_URL = "https://accounts.pixiv.net/login?lang=zh&source=pc&view_type=page&ref=wwwtop_accounts_index";

    /**
     * P站登录请求url
     */
    public static final String PIXIV_LOGIN_URL = "https://accounts.pixiv.net/api/login?lang=zh";

    /**
     * P站搜索请求url
     */
    private static final String PIXIV_SEARCH_URL = "https://www.pixiv.net/search.php";

    /**
     * P站搜索用户url
     * 需要替换的参数:
     * {nick} - 用户昵称、部分名称
     */
    public static final String PIXIV_SEARCH_USER_URL = PIXIV_SEARCH_URL + "?s_mode=s_usr&nick={nick}";

    /**
     * P站搜索插画url
     * 需要替换的参数:
     * {word} - 插画相关文本
     */
    public static final String PIXIV_SEARCH_TAG_URL = PIXIV_SEARCH_URL + "?s_mode=s_tag&word={word}";

    /**
     * P站插图下载链接获取url
     * 需要替换的文本:
     * {illustId} - 插画ID
     */
    public static final String PIXIV_ILLUST_API_URL = "https://www.pixiv.net/ajax/illust/{illustId}/pages";

    /*
     * P站用户插图列表获取API
     * 需要替换的文本:
     * {userId} - 用户ID
     */
    //TODO: 所需数据在 body属性内的 illusts(属性名,属性值不重要), manga(多图) pickup(精选)
    //{"error":false,"message":"","body":{"illusts":{"74369837":null,"70990542":null,"70608653":null,"69755191":null,"69729450":null,"69729416":null,"69503608":null,"69288766":null,"69083882":null,"69051458":null,"68484200":null,"68216927":null,"68216866":null,"68192333":null,"67915106":null,"67914932":null,"67854803":null,"67854745":null,"67854670":null,"67787211":null,"67772199":null,"67770637":null,"67754861":null,"67754804":null,"67754726":null,"67740486":null,"67740480":null,"67740450":null,"67740434":null,"67726337":null,"67499196":null,"67499163":null,"67499145":null,"67499111":null,"67499085":null,"67499038":null,"67498987":null,"67473178":null,"66271465":null,"63682753":null,"63682697":null,"59385148":null,"59383265":null,"59383240":null,"59383227":null,"59383173":null},"manga":[],"novels":[],"mangaSeries":[],"novelSeries":[],"pickup":[],"bookmarkCount":{"public":{"illust":1,"novel":0},"private":{"illust":0,"novel":0}}}}
    //public static final String PIXIV_USER_ILLUST_LIST_URL = "https://www.pixiv.net/ajax/user/{userId}/profile/all";

    /**
     * 能够同时获取插图信息的用户插图列表获取API
     */
    public static final String PIXIV_USER_ILLUSTINFO_LIST_URL = "https://www.pixiv.net/ajax/user/{userId}/profile/top";

    /**
     * P站单图详情页url
     * 需要替换的文本:
     * {illustId} - 插画ID
     */
    public static final String PIXIV_ILLUST_MEDIUM_URL = "https://www.pixiv.net/member_illust.php?mode=medium&illust_id={illustId}";

    /**
     * P站多图详情页url
     * 需要替换的文本:
     * {illustId} - 插画ID
     */
    public static final String PIXIV_ILLUST_MANGA_URL = "https://www.pixiv.net/member_illust.php?mode=manga&illust_id={illustId}";

    /**
     * P站用户页面url
     * 需要替换的文本:
     * {userId} - 用户ID
     */
    public static final String PIXIV_USER_URL = "https://www.pixiv.net/member.php?id={userId}";

    /**
     * P站插图信息获取API
     * 这个API能获取插图基本信息，但不能获取大小
     * 请使用{@link #getPixivIllustInfoAPI(int[])}获取URL
     */
    private static final String PIXIV_GET_ILLUST_INFO_URL = "https://www.pixiv.net/ajax/illust/recommend/illusts?";

    /**
     * P站获取用户所有插图ID的Api
     * 这个API只能获取该用户的插图ID，不能获取图片信息(图片信息要另外获取)
     * 需要替换的文本:
     * {userId} - 用户ID
     */
    public static final String PIXIV_GET_USER_ALL_ILLUST_ID_URL = "https://www.pixiv.net/ajax/user/{userId}/profile/all";

    /**
     * P站标签搜索URL
     * 可以将Tag的大概内容搜索成P站精确的Tag，以搜索其他接口返回的Tags数组;
     * 需要替换的文本:
     * {content} - 大致tag内容
     */
    public static final String PIXIV_TAG_SEARCH_URL = "https://www.pixiv.net/ajax/search/tags/{content}";

    /**
     * 请求时带上需要退出的Cookies
     * 无论成功与否都会返回302重定向到{@linkplain #PIXIV_LOGIN_PAGE_URL 登录页面}
     */
    public static final String PIXIV_LOGOUT_URL = "https://www.pixiv.net/logout.php";

    /**
     * 构造P站获取插图信息的Api Url
     * @param illustIds 要查询的插图ID数组
     * @return 对应查询的API Url
     */
    public static String getPixivIllustInfoAPI(int[] illustIds){
        StringBuilder strBuilder = new StringBuilder().append(PIXIV_GET_ILLUST_INFO_URL);
        for(int illustId : illustIds){
            strBuilder.append("illust_ids[]=").append(illustId).append("&");
        }
        return strBuilder.toString();
    }

    /**
     * 获取用于下载图片时防盗链所需Referer的链接
     * @param illustId 欲下载图片所属illustId
     * @return 返回Referer链接, 也可以作为作品链接使用
     */
    public static String getPixivRefererLink(int illustId){
        return "https://www.pixiv.net/artworks/" + illustId;
    }

    /**
     * 获取用于下载图片时防盗链所需Referer的链接
     * @param illustId 欲下载图片所属illustId
     * @return 返回Referer链接, 也可以作为作品链接使用
     */
    public static String getPixivRefererLink(String illustId){
        return "https://www.pixiv.net/artworks/" + illustId;
    }

    /**
     * 排行榜接口, 需加入"&format=json"
     */
    private final static String PIXIV_RANKING_LINK = "https://www.pixiv.net/ranking.php?";

    /**
     * 查询用户收藏.<br/>
     * 该URL返回HTML页面，需要进行解析.<br/>
     * 需要替换的文本:<br/>
     * {pageIndex} - 页数, 超出了则结果为空<br/>
     */
    public final static String PIXIV_USER_COLLECTION_PAGE = "https://www.pixiv.net/bookmark.php?rest=show&p={pageIndex}";

    /**
     * 获取排名榜
     * @param mode 查询类型, 详细信息看{@link RankingMode}, 如本参数为null, 则为每天
     * @param contentType 排名榜类型, 如为null则为综合
     * @param time 欲查询的时间, 最新只能查询昨天, 根据mode不同:
     *             每天 - 查询指定日期的排名榜
     *             每周 - 查询指定时间结束(含)到七天前一段时间内的排名榜
     *             每月 - 查询指定日期结束(含)到28天时间内的排名榜
     *             新人 - 与每周相同
     *             受男性欢迎 - 与每天相同
     *             受女性欢迎 - 与每天相同
     *        默认值为昨天
     * @param pageIndex 页数，一页50位，总共10页
     * @return 返回构建好的链接
     */
    public static String getRankingLink(RankingContentType contentType, RankingMode mode, Date time, int pageIndex, boolean json){
        StringBuilder linkBuilder = new StringBuilder(PIXIV_RANKING_LINK);
        linkBuilder.append("mode=").append(mode == null ? RankingMode.MODE_DAILY.modeParam : mode.modeParam);
        if(contentType != null && !contentType.equals(RankingContentType.ALL)){
            linkBuilder.append("&content=").append(contentType.typeName);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date queryDate;
        if(time == null){
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(new Date());
            gregorianCalendar.add(Calendar.DATE, -1);
            queryDate = gregorianCalendar.getTime();
        } else {
            queryDate = time;
        }
        linkBuilder.append("&date=").append(format.format(queryDate));
        if(pageIndex > 0 && pageIndex <= 10) {
            linkBuilder.append("&p=").append(pageIndex);
        }
        if(json) {
            linkBuilder.append("&format=").append("json");
        }
        return linkBuilder.toString();
    }

    /**
     * 排名榜模式
     */
    public enum RankingMode{
        /**
         * 每天
         */
        MODE_DAILY("daily"),
        /**
         * 每周
         */
        MODE_WEEKLY("weekly"),
        /**
         * 每月
         */
        MODE_MONTHLY("monthly"),
        /**
         * 新人
         */
        MODE_ROOKIE("rookie"),
        /**
         * 受男性喜欢
         */
        MODE_MALE("male"),
        /**
         * 受女性喜欢
         */
        MODE_FEMALE("female"),

        /**
         * 每天 - R18
         */
        MODE_DAILY_R18("daily_r18"),
        /**
         * 每周 - R18
         */
        MODE_WEEKLY_R18("weekly_r18"),
        /**
         * 受男性喜欢 - R18
         */
        MODE_MALE_R18("male_r18"),
        /**
         * 受女性喜欢 - R18
         */
        MODE_FEMALE_R18("female_r18"),
        ;
        public String modeParam;

        RankingMode(String modeParamName){
            this.modeParam = modeParamName;
        }
    }

    /**
     * Pixiv搜索接口.<br/>
     * 要使用该链接请使用{@link PixivSearchBuilder}构造链接.<br/>
     * 需要替换的参数: <br/>
     * content - 搜索内容
     */
    final static String PIXIV_SEARCH_CONTENT_URL = "https://www.pixiv.net/ajax/search/{area}/{content}?word={content}";

    /**
     * 排名榜类型
     */
    public enum RankingContentType{
        ALL("", RankingMode.values()),
        /**
         * 插画
         * 支持的时间类型: 每天, 每周, 每月, 新人
         */
        TYPE_ILLUST("illust",
                new RankingMode[]{
                        RankingMode.MODE_DAILY,
                        RankingMode.MODE_MONTHLY,
                        RankingMode.MODE_WEEKLY,
                        RankingMode.MODE_ROOKIE,
                        RankingMode.MODE_DAILY_R18,
                        RankingMode.MODE_WEEKLY_R18,
                        RankingMode.MODE_MALE_R18,
                        RankingMode.MODE_FEMALE_R18
                }
        ),
        /**
         * 动图
         * 支持的时间类型:每天, 每周
         */
        TYPE_UGOIRA("ugoira",
                new RankingMode[]{
                        RankingMode.MODE_DAILY,
                        RankingMode.MODE_WEEKLY,
                        RankingMode.MODE_DAILY_R18,
                        RankingMode.MODE_WEEKLY_R18
                }
        ),
        /**
         * 漫画
         * 支持的时间类型: 每天, 每周, 每月, 新人
         */
        TYPE_MANGA("manga",
                new RankingMode[]{
                        RankingMode.MODE_DAILY,
                        RankingMode.MODE_MONTHLY,
                        RankingMode.MODE_WEEKLY,
                        RankingMode.MODE_ROOKIE,
                        RankingMode.MODE_DAILY_R18,
                        RankingMode.MODE_WEEKLY_R18,
                        RankingMode.MODE_MALE_R18,
                        RankingMode.MODE_FEMALE_R18
                }
        )
        ;

        String typeName;

        private final RankingMode[] supportedMode;

        RankingContentType(String typeName, RankingMode[] supportedMode){
            this.typeName = typeName;
            this.supportedMode = supportedMode;
        }

        /**
         * 检查指定RankingMode是否支持
         * @param mode 要检查的RankingMode项
         * @return 如果支持返回true
         */
        public boolean isSupportedMode(RankingMode mode) {
            return Arrays.binarySearch(supportedMode, mode) != -1;
        }

    }

}
