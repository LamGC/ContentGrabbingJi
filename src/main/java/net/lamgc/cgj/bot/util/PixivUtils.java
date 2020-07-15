package net.lamgc.cgj.bot.util;

import com.google.common.base.Strings;
import net.lamgc.cgj.pixiv.PixivSearchLinkBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pixiv工具类
 */
public final class PixivUtils {

    private final static Logger log = LoggerFactory.getLogger(PixivUtils.class);

    private PixivUtils() {}

    /**
     * 快速构造一个PixivSearchLinkBuilder
     * @param content 搜索内容
     * @param type 搜索类型
     * @param area 搜索范围
     * @param includeKeywords 包含关键词
     * @param excludeKeywords 排除关键词
     * @param contentOption 内容级别选项
     * @param pageIndex 搜索页数
     * @return 返回PixivSearchLinkBuilder对象
     * @see PixivSearchLinkBuilder
     */
    public static PixivSearchLinkBuilder buildSearchLinkBuilder(
            String content,
            String type,
            String area,
            String includeKeywords,
            String excludeKeywords,
            String contentOption,
            int pageIndex
    ) {
        PixivSearchLinkBuilder searchBuilder = new PixivSearchLinkBuilder(Strings.isNullOrEmpty(content) ? "" : content);
        if (type != null) {
            try {
                searchBuilder.setSearchType(PixivSearchLinkBuilder.SearchType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchType: {}", type);
            }
        }
        if (area != null) {
            try {
                searchBuilder.setSearchArea(PixivSearchLinkBuilder.SearchArea.valueOf(area));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchArea: {}", area);
            }
        }
        if (contentOption != null) {
            try {
                searchBuilder.setSearchContentOption(
                        PixivSearchLinkBuilder.SearchContentOption.valueOf(contentOption.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchContentOption: {}", contentOption);
            }
        }

        if (!Strings.isNullOrEmpty(includeKeywords)) {
            for (String keyword : includeKeywords.split(";")) {
                searchBuilder.removeExcludeKeyword(keyword.trim());
                searchBuilder.addIncludeKeyword(keyword.trim());
                log.trace("已添加关键字: {}", keyword);
            }
        }
        if (!Strings.isNullOrEmpty(excludeKeywords)) {
            for (String keyword : excludeKeywords.split(";")) {
                searchBuilder.removeIncludeKeyword(keyword.trim());
                searchBuilder.addExcludeKeyword(keyword.trim());
                log.trace("已添加排除关键字: {}", keyword);
            }
        }

        if(pageIndex > 0) {
            searchBuilder.setPage(pageIndex);
        }

        return searchBuilder;
    }



}
