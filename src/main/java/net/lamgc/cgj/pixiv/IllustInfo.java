package net.lamgc.cgj.pixiv;

/**
 * 插图(集)的信息
 */
public class IllustInfo{

    /**
     * 插图(集)ID
     */
    public final int illustID;

    /**
     * 第几页
     */
    public final int page;

    /**
     * 插图标题
     */
    public final String title;

    /**
     * 插图说明
     */
    public final String description;

    /**
     * 插图标签
     */
    public final String[] tags;

    /**
     * 插图图片长度
     */
    public final int width;

    /**
     * 插图图片高度
     */
    public final int height;

    /**
     * 作者名
     */
    public final String authorName;

    /**
     * 作者用户ID
     */
    public final int authorUserID;

    public IllustInfo(int illustID, String title, String description, String[] tags, int width, int height, String authorName, int authorUserID){
        this(illustID, 0, title, description, tags, width, height, authorName, authorUserID);
    }

    public IllustInfo(int illustID, int p, String title, String description, String[] tags, int width, int height, String authorName, int authorUserID){
        this.illustID = illustID;
        this.page = p;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.width = width;
        this.height = height;
        this.authorName = authorName;
        this.authorUserID = authorUserID;
    }

}
