package net.lamgc.cgj.pixiv;

import com.google.common.base.Strings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

public class PixivSearchBuilder {

    private final String content;

    private SearchArea searchArea = SearchArea.ARTWORKS;
    private SearchMode searchMode = SearchMode.TAG_FULL;
    private SearchType searchType = SearchType.ILLUST_AND_UGOIRA;
    private SearchOrder searchOrder = SearchOrder.DATE_D;
    private SearchContentOption searchContentOption = SearchContentOption.ALL;

    private HashSet<String> includeKeywords = new HashSet<>(0);
    private HashSet<String> excludeKeywords = new HashSet<>(0);

    private int page = 1;

    private int wgt = 0;
    private int hgt = 0;

    private int wlt = 0;
    private int hlt = 0;

    private RatioOption ratioOption = null;

    private Date startDate = null;
    private Date endDate = null;

    public PixivSearchBuilder(String searchContent) {
        this.content = Objects.requireNonNull(searchContent);
    }

    public String buildURL() {
        StringBuilder builder;
        try {
            builder = new StringBuilder(PixivURL.PIXIV_SEARCH_CONTENT_URL.replaceAll("\\{area}", searchArea.name().toLowerCase())
                    .replaceAll("\\{content}",
                        URLEncoder.encode(getSearchCondition(), "UTF-8").replaceAll("\\+", "%20")
                    )
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if(searchArea.equals(SearchArea.TOP)) {
            return builder.toString();
        }

        builder.append("&s_mode=").append(searchMode.name().toLowerCase());
        builder.append("&type=").append(searchType.name().toLowerCase());
        builder.append("&p=").append(page);
        builder.append("&order=").append(searchOrder.name().toLowerCase());
        builder.append("&mode=").append(searchContentOption.name().toLowerCase());
        
        //可选参数
        if(wgt > 0 && hgt > 0) {
            builder.append("&wgt=").append(wgt);
            builder.append("&hgt").append(hgt);
        }

        //可选参数
        if(wlt > 0 && hlt > 0) {
            builder.append("&wlt=").append(wlt);
            builder.append("&hlt").append(hlt);
        }

        if (ratioOption != null) {
            builder.append("&ratio=").append(ratioOption.value);
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            builder.append("&scd=").append(format.format(startDate));
        }
        if (endDate != null) {
            builder.append("&ecd=").append(format.format(endDate));
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixivSearchBuilder that = (PixivSearchBuilder) o;
        return page == that.page &&
                wgt == that.wgt &&
                hgt == that.hgt &&
                wlt == that.wlt &&
                hlt == that.hlt &&
                content.equals(that.content) &&
                searchArea == that.searchArea &&
                searchMode == that.searchMode &&
                searchType == that.searchType &&
                searchOrder == that.searchOrder &&
                searchContentOption == that.searchContentOption &&
                includeKeywords.equals(that.includeKeywords) &&
                excludeKeywords.equals(that.excludeKeywords) &&
                ratioOption == that.ratioOption &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                content,
                searchArea,
                searchMode,
                searchType,
                searchOrder,
                searchContentOption,
                includeKeywords,
                excludeKeywords,
                page,
                wgt,
                hgt,
                wlt,
                hlt,
                ratioOption,
                startDate,
                endDate);
    }

    @Override
    public String toString() {
        return "PixivSearchBuilder{" +
                "content='" + content + '\'' +
                ", searchArea=" + searchArea +
                ", searchMode=" + searchMode +
                ", searchType=" + searchType +
                ", searchOrder=" + searchOrder +
                ", searchContentOption=" + searchContentOption +
                ", includeKeywords=" + includeKeywords +
                ", excludeKeywords=" + excludeKeywords +
                ", page=" + page +
                ", wgt=" + wgt +
                ", hgt=" + hgt +
                ", wlt=" + wlt +
                ", hlt=" + hlt +
                ", ratioOption=" + ratioOption +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    public PixivSearchBuilder setSearchArea(SearchArea searchArea) {
        this.searchArea = Objects.requireNonNull(searchArea);
        return this;
    }

    /**
     * 获取搜索区域
     * @return 返回搜索区域对象
     */
    public SearchArea getSearchArea() {
        return searchArea;
    }

    /**
     * 获取搜索条件.
     * @return 搜索条件内容
     */
    public String getSearchCondition() {
        StringBuilder searchContent = new StringBuilder(Strings.nullToEmpty(this.content));
        if(searchArea.equals(SearchArea.TOP)) {
            return searchContent.toString();
        }

        excludeKeywords.forEach(keyword -> searchContent.append(" -").append(keyword));
        if(!includeKeywords.isEmpty()) {
            if(!Strings.isNullOrEmpty(searchContent.toString())) {
                searchContent.append(" (");
            }
            includeKeywords.forEach(keyword -> searchContent.append(keyword).append(" OR "));
            int deleteStart = searchContent.lastIndexOf(" OR ");
            if(searchContent.length() >= 4 && deleteStart != -1) {
                searchContent.delete(deleteStart, searchContent.length());
            }
            if(!Strings.isNullOrEmpty(searchContent.toString())) {
                searchContent.append(")");
            }
        }

        return searchContent.toString();
    }

    public PixivSearchBuilder setSearchMode(SearchMode searchMode) {
        this.searchMode = Objects.requireNonNull(searchMode);
        return this;
    }

    public PixivSearchBuilder setSearchType(SearchType searchType) {
        this.searchType = Objects.requireNonNull(searchType);
        return this;
    }

    public PixivSearchBuilder setSearchOrder(SearchOrder searchOrder) {
        this.searchOrder = Objects.requireNonNull(searchOrder);
        return this;
    }

    public PixivSearchBuilder setSearchContentOption(SearchContentOption searchContentOption) {
        this.searchContentOption = Objects.requireNonNull(searchContentOption);
        return this;
    }

    public PixivSearchBuilder setRatioOption(RatioOption ratioOption) {
        this.ratioOption = Objects.requireNonNull(ratioOption);
        return this;
    }

    public PixivSearchBuilder setDateRange(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    public PixivSearchBuilder setMaxSize(int width, int height) {
        this.wgt = width;
        this.hgt = height;
        return this;
    }

    public PixivSearchBuilder setMinSize(int width, int height) {
        this.wlt = width;
        this.hlt = height;
        return this;
    }

    public PixivSearchBuilder setPage(int pageIndex) {
        if (pageIndex <= 0) {
            throw new IllegalArgumentException("Invalid pageIndex: " + pageIndex);
        }
        this.page = pageIndex;
        return this;
    }

    public PixivSearchBuilder addExcludeKeyword(String keyword) {
        excludeKeywords.add(keyword);
        return this;
    }

    public PixivSearchBuilder removeExcludeKeyword(String keyword) {
        excludeKeywords.remove(keyword);
        return this;
    }

    public PixivSearchBuilder addIncludeKeyword(String keyword) {
        includeKeywords.add(keyword);
        return this;
    }

    public PixivSearchBuilder removeIncludeKeyword(String keyword) {
        includeKeywords.remove(keyword);
        return this;
    }

    /**
     * 搜索区域
     */
    public enum SearchArea {
        /**
         * 所有(可能是 插画 + 漫画)
         */
        ARTWORKS("illustManga"),
        /**
         * 顶部(所有内容)
         * 同时包含了:
         * {@link #ILLUSTRATIONS}
         * {@link #MANGA}
         * {@link #NOVELS}
         * 选择此项后, 将直接显示所有与content相关内容, 而忽略所有附加搜索条件.
         * 因为无法指定pageIndex, 数据只有24项
         */
        TOP(null),
        /**
         * 插画
         */
        ILLUSTRATIONS("illust"),
        /**
         * 漫画
         */
        MANGA("manga"),
        /**
         * 小说
         */
        NOVELS("novel");

        public final String jsonKey;

        SearchArea(String jsonKey) {
            this.jsonKey = jsonKey;
        }


    }

    /**
     * 搜索模式
     */
    public enum SearchMode {
        /**
         * 按标签搜索, 部分一致
         */
        TAG,
        /**
         * 按标签搜索, 完全一致
         */
        TAG_FULL,
        /**
         * 按标题和说明文字搜索
         */
        TC
    }

    /**
     * 搜索内容类型
     */
    public enum SearchType {
        /**
         * 全部内容(插画、漫画、动图)
         */
        ALL,
        /**
         * 插画和动图(不包括漫画)
         */
        ILLUST_AND_UGOIRA,
        /**
         * 插图
         */
        ILLUST,
        /**
         * 漫画
         */
        MANGA,
        /**
         * 动图
         */
        UGOIRA
    }

    public enum SearchOrder {
        /**
         * 按旧排序
         */
        DATE,
        /**
         * 按新排序
         */
        DATE_D
    }

    /**
     * 搜索内容选项
     */
    public enum SearchContentOption {
        /**
         * 所有内容
         */
        ALL,
        /**
         * 全年龄
         */
        SAFE,
        /**
         * R18
         */
        R18
    }

    public enum RatioOption {
        /**
         * 横向
         */
        TRANSVERSE(0.5F),
        /**
         * 纵向
         */
        PORTRAIT(-0.5F),
        /**
         * 正方形
         */
        SQUARE(0F)
        ;

        public final float value;

        RatioOption(float ratio) {
            this.value = ratio;
        }

    }


}
