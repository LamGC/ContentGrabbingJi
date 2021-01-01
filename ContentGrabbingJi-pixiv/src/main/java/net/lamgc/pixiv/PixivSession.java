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

import org.apache.http.client.CookieStore;

import java.io.IOException;
import java.util.Objects;

/**
 * Pixiv 会话.
 * @author LamGC
 */
public abstract class PixivSession {

    private final CookieStore cookieStore;

    protected PixivSession(CookieStore cookieStore) {
        this.cookieStore = Objects.requireNonNull(cookieStore);
    }

    /**
     * 登出当前会话.
     *
     * <p>登出后该会话 Cookies 将会失效.
     * @return 如果登出成功, 返回 true.
     * @throws IOException 当尝试登出发生异常时抛出.
     */
    public abstract boolean logOut() throws IOException;

    /**
     * 检查是否已登录, 或者说会话是否有效.
     * @return 如果会话已登出(失效), 返回 false.
     */
    public abstract boolean isLogin();

    /**
     * 获取 CookieStore 对象.
     * @return 返回 CookieStore.
     */
    public CookieStore getCookies() {
        return cookieStore;
    }

}
