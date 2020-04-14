package net.lamgc.cgj.bot.cache;

import net.lamgc.utils.event.EventObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class ImageCacheObject implements EventObject {

    private final Map<String, File> imageCache;

    private final int illustId;

    private final String downloadLink;

    private final File storeFile;

    public ImageCacheObject(Map<String, File> imageCache, int illustId, String downloadLink, File storeFile) {
        this.imageCache = imageCache;
        this.illustId = illustId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageCacheObject that = (ImageCacheObject) o;
        return illustId == that.illustId &&
                Objects.equals(imageCache, that.imageCache) &&
                Objects.equals(downloadLink, that.downloadLink) &&
                Objects.equals(storeFile, that.storeFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageCache, illustId, downloadLink, storeFile);
    }

    @Override
    public String toString() {
        return "ImageCacheObject@" + Integer.toHexString(hashCode()) + "{" +
                "illustId=" + illustId +
                ", downloadLink='" + downloadLink + '\'' +
                ", storeFile=" + storeFile +
                '}';
    }
}
