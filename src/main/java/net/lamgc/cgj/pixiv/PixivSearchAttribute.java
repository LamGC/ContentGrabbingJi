package net.lamgc.cgj.pixiv;

/**
 * 搜索结果的属性枚举类.
 * <p>按照请求的{@link PixivSearchLinkBuilder.SearchArea}获取所支持的属性数组</p>
 */
public enum PixivSearchAttribute {

    ARTWORKS("illustManga"),
    TOP("illustManga", "novel"),
    ILLUSTRATIONS("illust"),
    MANGA("manga"),
    NOVELS("novel")
    ;

    public final String[] attributeNames;

    PixivSearchAttribute(String... attributeNames) {
        this.attributeNames = attributeNames;
    }

}
