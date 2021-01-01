/*
 * Copyright (C) 2021  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * ContentGrabbingJi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lamgc.pixiv;

import com.google.common.net.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

/**
 * 基本的 Pixiv 会话对象.
 * @author LamGC
 */
public class BasicPixivSession extends PixivSession {

    private final static String PIXIV_LOGOUT_URL = "https://www.pixiv.net/logout.php";
    private final HttpClient httpClient;
    private boolean login = true;

    /**
     * 构造一个会话对象.
     * @param cookieStore 会话 Cookies.
     * @param httpClient 用于请求登出的 HttpClient 对象.
     */
    public BasicPixivSession(CookieStore cookieStore, HttpClient httpClient) {
        super(cookieStore);
        this.httpClient = httpClient;
    }

    @Override
    public boolean logOut() throws IOException {
        HttpGet request = new HttpGet(PIXIV_LOGOUT_URL);
        StringBuilder builder = new StringBuilder();
        getCookies().getCookies().forEach(cookie ->
                builder.append(cookie.getName()).append("=").append(cookie.getValue()).append("; "));
        request.addHeader(HttpHeaders.COOKIE, builder.toString());

        HttpResponse response = httpClient.execute(request);
        final int httpRedirectCode = 302;
        if (response.getStatusLine().getStatusCode() == httpRedirectCode) {
            login = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean isLogin() {
        return login;
    }

}
