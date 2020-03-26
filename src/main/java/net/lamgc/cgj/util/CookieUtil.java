package net.lamgc.cgj.util;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.Date;

public class CookieUtil {

    /**
     * 将{@link java.net.CookieStore}转换到{@link CookieStore}
     * @param netCookieStore java.net.CookieStore
     * @return org.apache.http.client.CookieStore
     */
    public static CookieStore NAParse(java.net.CookieStore netCookieStore){
        CookieStore apacheCookieStore = new BasicCookieStore();
        netCookieStore.getCookies().forEach(netCookie -> {
            BasicClientCookie aCookie = new BasicClientCookie(netCookie.getName(), netCookie.getValue());
            aCookie.setComment(netCookie.getComment());
            aCookie.setDomain(netCookie.getDomain());
            aCookie.setExpiryDate(new Date(netCookie.getMaxAge()));
            aCookie.setPath(netCookie.getPath());
            aCookie.setSecure(netCookie.getSecure());
            aCookie.setVersion(netCookie.getVersion());
            apacheCookieStore.addCookie(aCookie);
        });
        return apacheCookieStore;
    }

}
