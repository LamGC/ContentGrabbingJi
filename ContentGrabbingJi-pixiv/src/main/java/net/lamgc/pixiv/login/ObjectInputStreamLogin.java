/*
 * Copyright (C) 2020  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ContentGrabbingJi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lamgc.pixiv.login;

import net.lamgc.pixiv.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 读入对象进行登录.
 * @author LamGC
 */
public class ObjectInputStreamLogin implements PixivLogin {

    private final HttpClient httpClient;
    private final ObjectInputStream inputStream;
    private final AtomicReference<PixivSession> sessionCache = new AtomicReference<>();

    public ObjectInputStreamLogin(HttpClient httpClient, InputStream cookieInput) throws IOException {
        this.httpClient = httpClient;
        this.inputStream = new ObjectInputStream(cookieInput);
    }


    @Override
    public synchronized PixivSession login() throws PixivLoginException {
        if (sessionCache.get() == null) {
            CookieStore cookieStore;
            try {
                cookieStore = (CookieStore) inputStream.readObject();
            } catch (Exception e) {
                throw new PixivLoginException("An exception occurred while trying to read in a Java serialized object", e);
            }
            PixivSession session = new BasicPixivSession(cookieStore, this.httpClient);
            sessionCache.set(session);
            return session;
        } else {
            return sessionCache.get();
        }

    }
}
