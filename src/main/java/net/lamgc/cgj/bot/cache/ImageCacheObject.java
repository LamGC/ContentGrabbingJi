package net.lamgc.cgj.bot.cache;

import net.lamgc.utils.event.EventObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class ImageCacheObject implements EventObject {

    private final Map<String, File> imageCache;

    private final int illustId;

    private final int pageIndex;

    private final String downloadLink;

    private final File storeFile;

    public ImageCacheObject(Map<String, File> imageCache, int illustId, int pageIndex, String downloadLink, File storeFile) {
        this.imageCache = imageCache;
        this.illustId = illustId;
        this.pageIndex = pageIndex;
        this.downloadLink = downloadLink;
        this.storeFile = storeFile;
    }

    public Map<String, File> getImageCache() {
        return imageCache;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public File getStoreFile() {
        return storeFile;
    }

    public int getIllustId() {
        return illustId;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageCacheObject that = (ImageCacheObject) o;
        return illustId == that.illustId &&
                pageIndex == that.pageIndex &&
                Objects.equals(imageCache, that.imageCache) &&
                Objects.equals(downloadLink, that.downloadLink) &&
                Objects.equals(storeFile, that.storeFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageCache, illustId, pageIndex, downloadLink, storeFile);
    }

    @Override
    public String toString() {
        return "ImageCacheObject{" +
                "imageCache=" + imageCache +
                ", illustId=" + illustId +
                ", pageIndex=" + pageIndex +
                ", downloadLink='" + downloadLink + '\'' +
                ", storeFile=" + storeFile +
                '}';
    }
}
