package net.lamgc.cgj.pixiv;

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
