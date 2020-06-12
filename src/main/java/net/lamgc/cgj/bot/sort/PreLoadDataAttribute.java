package net.lamgc.cgj.bot.sort;

@SuppressWarnings("unused")
public enum PreLoadDataAttribute {
    /**
     * 按点赞数排序
     */
    LIKE("likeCount"),

    /**
     * 按页面数排序
     */
    PAGE("pageCount"),

    /**
     * 按收藏数排序
     */
    BOOKMARK("bookmarkCount"),

    /**
     * 按评论数排序
     */
    COMMENT("commentCount"),

    /**
     * 不明
     */
    RESPONSE("responseCount"),

    /**
     * 按查看次数排序
     */
    VIEW("viewCount"),
    ;

    public final String attrName;

    PreLoadDataAttribute(String attrName) {
        this.attrName = attrName;
    }
}