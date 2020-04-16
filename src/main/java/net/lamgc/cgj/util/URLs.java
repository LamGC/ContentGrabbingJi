package net.lamgc.cgj.util;

public final class URLs {

    private URLs() {}

    public static String getResourceName(String url) {
        int startIndex = Math.max(url.lastIndexOf("\\"), url.lastIndexOf("/"));
        return url.substring(startIndex + 1);
    }
    
    
}
